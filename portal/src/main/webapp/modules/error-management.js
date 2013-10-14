define([ "message-bus" ], function(bus) {
	bus.subscribe("error", function(event, msg) {
		window.alert(msg);
	});
});
