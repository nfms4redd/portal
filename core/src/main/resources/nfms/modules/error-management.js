define([ "message-bus", "module", "jquery" ], function(bus, module) {
	bus.listen("error", function(event, msg) {

		var divId = module.config()["div-id"];
		if (divId != null) {
			$("#" + divId).html(msg);
		} else {
			window.alert(msg);
		}
	});
});
