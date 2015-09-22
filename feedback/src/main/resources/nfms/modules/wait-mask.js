define([ "message-bus", "jquery" ], function(bus, $) {

	bus.listen("show-wait-mask", function(event, message) {
		var background = $('<div/>').addClass("wait-mask").appendTo($("body"));
		var center = $('<div/>').appendTo(background);
		$('<div/>').html(message).appendTo(center);
	});

	bus.listen("hide-wait-mask", function(event) {
		$('.wait-mask').remove();
	});

	// Hide the mask when all modules are loaded
	bus.listen("modules-loaded", function() {
		bus.send("hide-wait-mask");
	});
	
	// Hide the hardcoded mask and show the one created by this module
	bus.send("hide-wait-mask");
	bus.send("show-wait-mask", "Cargando aplicación");
});
