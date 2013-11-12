define([ "layout", "jquery", "map", "layer-list", "limit"], function(layout, $, map, layerList, limit) {

	var info = $("#" + layout.infoId);

	$("<label/>").attr({
		"for" : "url"
	}).html("URL:").appendTo(info);
	var url = $("<input/>").attr({
		type : "text",
		id : "url",
		name : "url"
	}).val("http://www.geoportaligm.gob.ec/nacional/wms").appendTo(info);
	$("<label/>").attr({
		"for" : "layer"
	}).html("Layer:").appendTo(info);
	var layerName = $("<input/>").attr({
		type : "text",
		id : "layer",
		name : "layer"
	}).appendTo(info);

	$("<button/>").click(function() {
		var layer = new OpenLayers.Layer.WMS(name, url.val(), {
			layers : layerName.val(),
			transparent : true
		});
		limit.addLayer(layer);
		map.addLayer(layer);
		layerList.addLayer(layerName.val());
	}).html("Añadir").appendTo(info);

});