define([ "message-bus", "layout", "jquery", "toolbar" ], function(bus, layout, $) {
	bus.send("css-load", "modules/new-group-layer.css");

	var btnNewLayer = $("<a href='#'/>").html("Añadir mi capa").appendTo($("#" + layout.toolbarId));
	btnNewLayer.attr("id", "new-group-layer-button");
	btnNewLayer.addClass("blue_button lang_button");
	btnNewLayer.click(function() {
		bus.send("add-group", {
			"id" : "spain",
			"name" : "Datos de España"
		});
		bus.send("add-layer", {
			"id" : "spain-catastro",
			"groupId" : "spain",
			"url" : "http://ovc.catastro.meh.es/Cartografia/WMS/ServidorWMS.aspx",
			"wmsName" : "Catastro",
			"name" : "Catastro",
			"visible" : "true"
		});
	});

});