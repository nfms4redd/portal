define([ "message-bus", "layout", "jquery", "toolbar" ], function(bus, layout, $) {

	var btnNewLayer = $("<a href='#'/>").html("Añadir mi capa").appendTo($("#" + layout.toolbarId));
	btnNewLayer.attr("id", "new-group-layer-button");
	btnNewLayer.addClass("blue_button lang_button");
	btnNewLayer.click(function() {
		bus.send("add-group", {
			"id" : "meteo",
			"name" : "Datos meteorológicos"
		});
		bus.send("add-layer", {
			"id" : "meteo-eeuu",
			"groupId" : "meteo",
			"url" : "http://mesonet.agron.iastate.edu/cgi-bin/wms/nexrad/n0r-t.cgi",
			"wmsName" : "nexrad-n0r-wmst",
			"name" : "EEUU",
			"visible" : "true"
		});
	});

});