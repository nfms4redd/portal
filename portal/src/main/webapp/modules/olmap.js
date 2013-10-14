define([ "message-bus", "layout", "openlayers" ], function(bus, layout) {
	var map = null;
	
	OpenLayers.ProxyHost = "proxy?url=";

	map = new OpenLayers.Map(layout.mapId, {
		"allOverlays" : true
	});

	bus.subscribe("initial-zoom", function(event, layerInfo) {
		map.zoomToMaxExtent();
	});

	bus.subscribe("add-layer", function(event, layerInfo) {
		var layer = new OpenLayers.Layer.WMS("WMS layer", layerInfo.url, {
			layers : layerInfo.wmsName,
			transparent : true
		});
		layer.id = layerInfo.id;
		if (!layerInfo.visible) {
			layer.setVisibility(false);
		}
		if (map !== null) {
			map.addLayer(layer);
		}
		bus.publish("maplayer-added", [ layer, layerInfo ]);
	});

	bus.subscribe("layer-visibility", function(event, layerId, visibility) {
		var layer = map.getLayer(layerId);
		layer.setVisibility(visibility);
	});

	return map;
});