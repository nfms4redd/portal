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

	var getValueOrDefault = function(object, value, defaultValue) {
		var objectValue = object[value];
		if (objectValue !== undefined) {
			return objectValue;
		} else {
			return defaultValue;
		}
	};

	require([ "customization" ]);

	$(document).bind(
			"customization-received",
			function(event, customization) {

				var languageCode = customization.languageCode;
				require([ "jquery", "iso8601", "css-loader", "layout", "error-management" ], function($) {
					$(document).trigger("css-load", "styles/jquery-ui-1.8.16.custom.css");
					$(document).trigger("css-load", "styles/jquery.fancybox.css");

					$.ajax({
						dataType : "json",
						url : "layers",
						data : "lang=" + languageCode,
						success : function(data, textStatus, jqXHR) {
							var groups = data.groups;
							for (var i = 0; i < groups.length; i++) {
								var group = groups[i];
								$(document).trigger("add-group", {
									"id" : group.id,
									"name" : group.label
								});

								var items = group.items;
								for (var j = 0; j < items.length; j++) {
									var matches = $.grep(data.portalLayers, function(l) {
										return l.id == items[j];
									});
									if (matches.length == 0) {
										$(document).trigger("trigger", "error",
												"No portal layer with id '" + items[j] + "', referenced by group " + group.id);
									} else if (matches.length > 1) {
										$(document).trigger("trigger", "error", "Two portal layers with id '" + items[j]);
									} else {
										var portalLayer = matches[0];
										$(document).trigger("add-layer", {
											"id" : portalLayer.id,
											"groupId" : group.id,
											"url" : "http://rdc-snsf.org/diss_geoserver/wms",
											"wmsName" : "common:blue_marble",
											"name" : portalLayer.label,
											"infoLink" : "http://rdc-snsf.org/static/loc/en/html/bluemarble_def.html",
											"timestamps" : [ "2001-2-23", "2003-5-12" ],
											"visible" : getValueOrDefault(portalLayer, "active", true)
										});
									}
								}
							}
						},
						error : function(jqXHR, textStatus, errorThrown) {
							$(document).trigger("error", jqXHR.responseText);
						}
					});

					$(document).trigger("initial-zoom");
				});

			});
});