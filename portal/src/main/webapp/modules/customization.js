define([ "module" ], function(module) {
	var customizationInfo = module.config();

	require(customizationInfo.modules, function(){
		require([ "layers" ]);
	});

	return customizationInfo;
});
