define([ "message-bus" ], function(bus) {
	bus.listen("error", function(event, msg) {
		window.alert(msg);
	});
});
