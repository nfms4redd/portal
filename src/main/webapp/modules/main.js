require.config({
	baseUrl : "modules",
	// uncomment this line for debugging purposes in order to bust cache
	urlArgs : "bust=" + (new Date()).getTime(),
	paths : {
		"jquery" : "http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min",
		"jquery-ui" : "http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min",
		"fancy-box" : "../js/jquery.fancybox.pack",
		"openlayers" : "../js/OpenLayers/OpenLayers.debug",
		"leaflet" : "http://cdn.leafletjs.com/leaflet-0.6.4/leaflet"
	}
});

require([ "jquery", "message-bus" ], function($, bus) {

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

	bus.listen("customization-received", function(event, customization) {
		require(customization.modules, function() {
			bus.send("css-load", "styles/jquery-ui-1.10.3.custom.css");
			bus.send("css-load", "styles/jquery.fancybox.css");

			bus.send("ajax", {
				dataType : "json",
				url : "layers",
				success : function(data, textStatus, jqXHR) {
					var groups = data.groups;
					for (var i = 0; i < groups.length; i++) {
						var group = groups[i];
						bus.send("add-group", {
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
									bus.send("add-layer", {
										"id" : portalLayer.id,
										"groupId" : group.id,
										"url" : url,
										"wmsName" : wmsName,
										"name" : portalLayer.label,
										"infoLink" : "http://rdc-snsf.org/static/loc/en/html/bluemarble_def.html",
										"timestamps" : [ "2001-2-23", "2003-5-12" ],
										"queryable" : wmsLayer.queryable,
										"visible" : getValueOrDefault(portalLayer, "active", true)
									});
								} else {
									bus.send("trigger", "error", "One (and only one) wms layer with id '" + id + "' expected");
								}

							} else {
								bus.send("trigger", "error", "One (and only one) portal layer with id '" + id + "' expected");
							}
						}

						bus.send("initial-zoom");
					}
				},
				errorMsg : "Cannot obtain layers from the server"
			});

		});

	});
});