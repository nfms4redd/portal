define([ "module" ], function(module) {
	var params = module.config();
	return {
		get : function(name) {
			var valueArray = params[name];
			if (valueArray) {
				return valueArray[0];
			} else {
				return null;
			}
		}
	};
});