define([ "message-bus", "layout", "openlayers" ], function(bus, layout) {
	var map = null;
	
	OpenLayers.ProxyHost = "proxy?url=";

	map = new OpenLayers.Map(layout.mapId, {
		"allOverlays" : true
	});

	bus.listen("initial-zoom", function(event, layerInfo) {
		map.zoomToMaxExtent();
	});

	bus.listen("add-layer", function(event, layerInfo) {
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
		bus.send("maplayer-added", [ layer, layerInfo ]);
	});

	bus.listen("layer-visibility", function(event, layerId, visibility) {
		var layer = map.getLayer(layerId);
		layer.setVisibility(visibility);
	});

	return map;
});