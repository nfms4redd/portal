define(["message-bus", "jquery"], function(bus, $) {

	bus.listen("wait-mask", function(event, message) {

		if (message) {
			// Display a message, blocking the UI
			var background = $('<div/>').addClass("wait-mask").appendTo($("body"));
			var center = $('<div/>').appendTo(background);
			$('<div/>').html(message).appendTo(center);
		} else {
			// Remove the wait-mask
			$('.wait-mask').remove();
		}

	});

});
