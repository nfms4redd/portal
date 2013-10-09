/*
 * Queries the server and launches an "customization" event
 */
define([ "jquery" ], function() {

	var customizationInfo = {};

	/*
	 * The language code is in the answer to this call, so at this moment we can
	 * only get it from the URL
	 */
	var data = "";
	var langParameter = decodeURIComponent((new RegExp('[?|&]lang=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [ , "" ])[1]
			.replace(/\+/g, '%20'))
			|| null;
	if (langParameter !== null) {
		data = "lang=" + langParameter;
	}

	$.ajax({
		dataType : "json",
		url : "customization",
		data : data,
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

	return customizationInfo;
});