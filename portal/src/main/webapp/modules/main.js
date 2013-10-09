require.config({
	baseUrl : "modules",
	paths : {
		"jquery" : "http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min",
		"jquery-ui" : "http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min",
		"fancy-box" : "../js/jquery.fancybox.pack",
		"openlayers" : "http://openlayers.org/dev/OpenLayers",
		"leaflet" : "http://cdn.leafletjs.com/leaflet-0.6.4/leaflet"
	}
});

require([ "jquery" ], function($) {

	require([ "customization" ]);

	$(document).bind("customization-received", function(event, customization) {
		
		var languageCode = customization.languageCode;
		require([ "jquery", "iso8601", "css-loader", "layout", "error-management" ], function($) {
			$(document).trigger("css-load", "styles/jquery-ui-1.8.16.custom.css");
			$(document).trigger("css-load", "styles/jquery.fancybox.css");

			$.ajax({
				dataType : "json",
				url : "layers",
				data : "lang=" + languageCode,
				success : function(data, textStatus, jqXHR) {
					console.log("here are the layers");
				},
				error : function(jqXHR, textStatus, errorThrown) {
					$(document).trigger("error", jqXHR.responseText);
				}
			});

			/*
			 * Queries the server and launches add-group and add-layer events
			 */
			$(document).trigger("add-group", {
				"id" : "basic",
				"name" : "Basic layers"
			});
			$(document).trigger("add-group", {
				"id" : "drc",
				"name" : "République Démocratique du Congo"
			});
			$(document).trigger("add-layer", {
				"id" : "blumarble",
				"groupId" : "basic",
				"url" : "http://rdc-snsf.org/diss_geoserver/wms",
				"wmsName" : "common:blue_marble",
				"name" : "Blue marble",
				"infoLink" : "http://rdc-snsf.org/static/loc/en/html/bluemarble_def.html",
				"visible" : "true"
			});
			$(document).trigger("add-layer", {
				"id" : "forest_classification",
				"groupId" : "drc",
				"url" : "http://rdc-snsf.org/diss_geoserver/wms",
				"wmsName" : "drc:facet_forest_classification",
				"name" : "FACET Forest Classification",
				"timestamps" : [ "2001-2-23", "2003-5-12" ],
				"visible" : false
			});

			$(document).trigger("initial-zoom");
		});

	});
});