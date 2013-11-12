define([ "message-bus", "layout", "openlayers" ], function(bus, layout) {
	var map = null;
	var currentControl = null;
	var defaultExclusiveControl = null;
	var timeDependentLayers = [];

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

	map = new OpenLayers.Map(layout.mapId, {
		theme : null,
		projection : new OpenLayers.Projection("EPSG:900913"),
		displayProjection : new OpenLayers.Projection("EPSG:4326"),
		units : "m",
		allOverlays : true,
		controls : []
	});
	map.addControl(new OpenLayers.Control.Navigation());

	bus.listen("add-layer", function(event, layerInfo) {
		var layer = new OpenLayers.Layer.WMS("WMS layer", layerInfo.url, {
			layers : layerInfo.wmsName,
			buffer : 0,
			transitionEffect : "resize",
			removeBackBufferDelay : 0,
			isBaseLayer : false,
			transparent : true
		});
		layer.id = layerInfo.id;
		if (!layerInfo.visible) {
			layer.setVisibility(false);
		}
		if (layerInfo.hasOwnProperty("timestamps")) {
			timeDependentLayers.push(layer);
		}
		if (map !== null) {
			map.addLayer(layer);
		}
		bus.send("maplayer-added", [ layer, layerInfo ]);
	});

	bus.listen("layer-visibility", function(event, layerId, visibility) {
		var layer = map.getLayer(layerId);
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

	bus.listen("time-slider.selection", function(event, date) {
		for (var i = 0; i < timeDependentLayers.length; i++) {
			var timeDependentLayer = timeDependentLayers[i];
			timeDependentLayer.mergeNewParams({
				"time" : date.toISO8601String()
			});
		}
	});

	return map;
});