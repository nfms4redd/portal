define([ "jquery" ], function($) {

	var messageBus = {};
	
	return {
		send : function(name, parameters) {
			$(messageBus).trigger(name, parameters);
		},
		listen : function(name, callBack) {
			$(messageBus).bind(name, callBack);
		}
	};
});