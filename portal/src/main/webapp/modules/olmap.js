define([ "jquery", "layout", "openlayers" ], function($, layout) {
	var map = null;

	map = new OpenLayers.Map(layout.mapId, {
		"allOverlays" : true
	});

	$(document).bind("initial-zoom", function(event, layerInfo) {
		map.zoomToMaxExtent();
	});

	$(document).bind("add-layer", function(event, layerInfo) {
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
		$(document).trigger("maplayer-added", [ layer, layerInfo ]);
	});

	$(document).bind("layer-visibility", function(event, layerId, visibility) {
		var layer = map.getLayer(layerId);
		layer.setVisibility(visibility);
	});

	return map;
});