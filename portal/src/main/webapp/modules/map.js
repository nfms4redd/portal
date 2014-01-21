define([ "message-bus", "layout", "openlayers" ], function(bus, layout) {
	var map = null;
	var currentControl = null;
	var defaultExclusiveControl = null;

	var activateExclusiveControl = function(event, control) {
		if (currentControl != null) {
			currentControl.deactivate();
			map.removeControl(currentControl);
		}

		map.addControl(control);
		control.activate();

		currentControl = control;
	};

	OpenLayers.ProxyHost = "proxy?url=";

	map = new OpenLayers.Map(layout.map.attr("id"), {
		theme : null,
		projection : new OpenLayers.Projection("EPSG:900913"),
		displayProjection : new OpenLayers.Projection("EPSG:4326"),
		units : "m",
		allOverlays : true,
		controls : []
	});
	map.addControl(new OpenLayers.Control.Navigation());
	map.addControl(new OpenLayers.Control.Scale());

	bus.listen("add-layer", function(event, layerInfo) {
		var layer = new OpenLayers.Layer.WMS("WMS layer", layerInfo.baseUrl, {
			layers : layerInfo.wmsName,
			buffer : 0,
			transitionEffect : "resize",
			removeBackBufferDelay : 0,
			isBaseLayer : false,
			transparent : true
		});
		layer.id = layerInfo.id;
//		if (!layerInfo.visible) {
//			layer.setVisibility(false);
//		}
		if (map !== null) {
			map.addLayer(layer);
		}
		bus.send("maplayer-added", [ layer, layerInfo ]);
	});

	bus.listen("layers-loaded", function() {
		// Add the vector layer for highlighted features on top of all the other layers

		// StyleMap for the highlight layer
		var styleMap = new OpenLayers.StyleMap({'strokeWidth': 5, fillOpacity: 0, strokeColor: '#ee4400', strokeOpacity: 0.5, strokeLinecap: 'round'});

		var highlightLayer = new OpenLayers.Layer.Vector("Highlighted Features", {styleMap: styleMap});
		highlightLayer.id = "Highlighted Features";
		map.addLayer(highlightLayer);
	});

	bus.listen("layer-visibility", function(event, layerInfo, visibility) {
		var layer = map.getLayer(layerInfo.id);
		layer.setVisibility(visibility);
	});

	bus.listen("activate-exclusive-control", function(event, control) {
		activateExclusiveControl(event, control);
	});

	bus.listen("activate-default-exclusive-control", function(event) {
		activateExclusiveControl(event, defaultExclusiveControl);
	});

	bus.listen("set-default-exclusive-control", function(event, control) {
		defaultExclusiveControl = control;
	});

	bus.listen("layer-timestamp-selected", function(event, layerId, timestamp) {
		var layer = map.getLayer(layerId);
		/*
		 * On application startup some events can be produced before the map has
		 * the reference to the layers so we have to check if layer is null
		 */
		if (layer !== null && timestamp !== null) {
			layer.mergeNewParams({'time': timestamp.toISO8601String()});
		}
	});

	bus.listen("zoom-in", function(event) {
		map.zoomIn();
	});

	bus.listen("zoom-out", function(event) {
		map.zoomOut();
	});

	bus.listen("zoom-to", function(event, bounds) {
		map.zoomToExtent(bounds);
	});

	bus.listen("transparency-slider-changed", function(event, layerInfo, opacity) {
		var layer = map.getLayer(layerInfo.id);
		layer.setOpacity(opacity);
	});

	return map;
});