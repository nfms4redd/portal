define([ "layout", "jquery", "message-bus" ], function(layout, $, bus) {

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
		bus.send("layer", [ {
			url : url.val(),
			name : layerName.val()
		} ]);
	}).html("Añadir").appendTo(info);

});