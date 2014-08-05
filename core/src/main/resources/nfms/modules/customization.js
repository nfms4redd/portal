define([ "module", "message-bus" ], function(module, bus) {
	var customizationInfo = module.config();

	require(customizationInfo.modules, function() {
		bus.send("modules-loaded");
	});

	return customizationInfo;
});
