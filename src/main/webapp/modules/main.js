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

	var findById = function(array, id) {
		var matches = $.grep(array, function(l) {
			return l.id == id;
		});
		if (matches.length == 1) {
			return matches[0];
		} else {
			return null;
		}
	};

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
					var groups = data.groups;
					for (var i = 0; i < groups.length; i++) {
						var group = groups[i];
						$(document).trigger("add-group", {
							"id" : group.id,
							"name" : group.label
						});

						var items = group.items;
						for (var j = 0; j < items.length; j++) {
							var portalLayer = findById(data.portalLayers, items[j]);
							if (portalLayer != null) {
								var wmsLayer = findById(data.wmsLayers, portalLayer.layers[0]);
								if (wmsLayer != null) {
									var url = wmsLayer.baseUrl;
									var wmsName = wmsLayer.wmsName;
									$(document).trigger("add-layer", {
										"id" : portalLayer.id,
										"groupId" : group.id,
										"url" : url,
										"wmsName" : wmsName,
										"name" : portalLayer.label,
										"infoLink" : "http://rdc-snsf.org/static/loc/en/html/bluemarble_def.html",
										"timestamps" : [ "2001-2-23", "2003-5-12" ],
										"visible" : getValueOrDefault(portalLayer, "active", true)
									});
								} else {
									$(document).trigger("trigger", "error", "One (and only one) wms layer with id '" + id + "' expected");
								}

							} else {
								$(document).trigger("trigger", "error", "One (and only one) portal layer with id '" + id + "' expected");
							}
						}
						
						$(document).trigger("initial-zoom");
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					$(document).trigger("error", jqXHR.responseText);
				}
			});

		});

	});
});