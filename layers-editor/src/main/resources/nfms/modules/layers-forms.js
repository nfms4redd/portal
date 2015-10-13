define(["text!resources/layers.schema.json", "jquery", "jquery-ui"], function(layers_shema, $) {

	var schema = JSON.parse(layers_shema);
	console.log(schema);
	
	var field = function(type, value) {
		console.log([type, value]);
		if (type.type == "string") {
			var div = $("<div/>").text(type.title + ": ").appendTo(form);
			var input = $("<input/>").attr("type", "text").attr("value", value).appendTo(div);
		}
	}
	
	var toc = function() {
		var props = schema.definitions.toc.properties;
		var vals = {
            "id": "registroredd",
            "label": "Registro REDD+",
            "infoFile": "registro_redd.html"
		}
		for(var i in props) {
			field(props[i], vals[i]);
		}
	}
	
	var dialog = $("<div/>");
	dialog.dialog({
		closeOnEscape : false,
		autoOpen : false,
		height : 500,
		minHeight : 400,
		width : 325,
		zIndex : 2000,
		resizable : true
	});
	dialog.dialog("open");
	var form = $("<form/>");
	dialog.append(form);
	toc();

});
