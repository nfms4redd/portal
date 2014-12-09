define([ "message-bus", "module", "jquery" ], function(bus, module) {

	var messageHandler = function(event, msg) {

		var divId = module.config()["div-id"];
		if (divId != null) {
			$("#" + divId).html(msg);
		} else {
			window.alert(msg);
		}
	};
	
	bus.listen("error", messageHandler);
	bus.listen("info", messageHandler);
});
