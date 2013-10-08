define([ "openlayers", "jquery" ], function(ol, $) {
	var map = null;

	$(document).bind("init-map", function(event, div) {
		var divMap = $("<div/>").attr("id", "map");
		div.append(divMap);

		map = new OpenLayers.Map("map", {
			"allOverlays" : true
		});
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
	});

	$(document).bind("layer-visibility", function(event, layerId, visibility) {
		var layer = map.getLayer(layerId);
		layer.setVisibility(visibility);
	});

});