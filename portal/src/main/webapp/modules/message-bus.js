define([ "jquery" ], function($) {

	var messageBus = {};
	
	return {
		publish : function(name, parameters) {
			$(messageBus).trigger(name, parameters);
		},
		subscribe : function(name, callBack) {
			$(messageBus).bind(name, callBack);
		}
	};
});