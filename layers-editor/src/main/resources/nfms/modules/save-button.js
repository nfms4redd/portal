define([ "message-bus", "toolbar", "jquery" ], function(bus, toolbar, $) {

	var btn = $("<a/>")//
	.attr("id", "save-layers-button")//
	.addClass("blue_button")//
	.html("Guardar capas")//
	.click(function() {
		bus.send("save-layers");
	})//
	.appendTo(toolbar);

});