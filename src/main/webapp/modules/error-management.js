define([ "jquery" ], function($) {
	$(document).bind("error", function(event, msg) {
		window.alert(msg);
	});
});
