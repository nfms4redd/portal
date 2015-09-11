define(["message-bus", "jquery"], function(bus, $) {

	bus.listen("show-wait-mask", function(event, message) {
		var background = $('<div/>').addClass("wait-mask").appendTo($("body"));
		var center = $('<div/>').appendTo(background);
		$('<div/>').html(message).appendTo(center);
	});

	bus.listen("hide-wait-mask", function(event) {
		$('.wait-mask').remove();
	});

});
