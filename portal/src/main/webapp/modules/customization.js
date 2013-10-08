/*
 * Queries the server and launches an "customization" event
 */
define([ "jquery" ], function() {

	var customizationInfo = {};

	$.ajax({
		dataType : "json",
		url : "customization",
		success : function(data, textStatus, jqXHR) {
			for ( var attr in data) {
				if (data.hasOwnProperty(attr)) {
					customizationInfo[attr] = data[attr];
				}
			}
			document.title = customizationInfo.title;
			$(document).trigger("customization-received");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$(document).trigger("error", "Cannot initialize application: " + textStatus + "->" + errorThrown);
		}
	});

	// var customizationInfo = {
	// "title" : "Portal del SNMB del país de ejemplo",
	// "languages" : [ "en", "fr", "es" ],
	// "languageCode" : "en",
	// "messages" : {
	// "title" : "Portal del SNMB del país de ejemplo",
	// "en" : "English",
	// "fr" : "Français",
	// "es" : "Español",
	// "legend_button" : "Legend",
	// "sustainable_management" : "Sustainable Forest Management",
	// "redd_plus_registry" : "REDD+ Registry",
	// "changes" : "Changes",
	// "months" : '["Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July",
	// "Aug.", "Sep.", "Oct.", "Nov.", "Dec."]'
	// }
	// };

	return customizationInfo;
});