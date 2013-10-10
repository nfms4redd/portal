/*
 * Queries the server and launches an "customization" event
 */
define([ "jquery", "communication" ], function() {

	var customizationInfo = {};

	$(document).trigger("ajax", {
		dataType : "json",
		url : "customization",
		success : function(data, textStatus, jqXHR) {
			for ( var attr in data) {
				if (data.hasOwnProperty(attr)) {
					customizationInfo[attr] = data[attr];
				}
			}
			document.title = customizationInfo.title;
			$(document).trigger("customization-received", customizationInfo);
		},
		errorMsg : "Cannot initialize application"
	});

	return customizationInfo;
});