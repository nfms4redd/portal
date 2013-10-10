/*
 * Performs the queries to the server providing a common error management
 * and including some parameters that should always be there, like 'lang'
 */
define([ "jquery" ], function() {

	$(document).bind(
			"ajax",
			function(event, ajaxParams) {
				/*
				 * The language code is not always available, so we can only get
				 * it from the URL
				 */
				var langParameter = "";
				var langParameterValue = decodeURIComponent((new RegExp('[?|&]lang=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [ , "" ])[1]
						.replace(/\+/g, '%20'))
						|| null;
				if (langParameterValue !== null) {
					langParameter = "lang=" + langParameterValue;
				}

				if (ajaxParams.hasOwnProperty("data")) {
					ajaxParams.data += "&";
				} else {
					ajaxParams.data = "";
				}
				ajaxParams.data += langParameter;

				ajaxParams.error = function(jqXHR, textStatus, errorThrown) {
					$(document).trigger("error", ajaxParams.errorMsg + ". " + jqXHR.responseText + ".");
				};

				$.ajax(ajaxParams);

			});
});