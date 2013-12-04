define([ "jquery", "message-bus", "customization", "module" ], function($, bus, customization, module) {

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

	bus.listen("modules-loaded", function() {
		var layerRoot = module.config();
		var groups = layerRoot.groups;
		for (var i = 0; i < groups.length; i++) {
			var group = groups[i];
            var groupInfo = {
                "id" : group.id,
                "name" : group.label
            };
            if (group.hasOwnProperty("infoFile")) {
                groupInfo.infoLink = "static/loc/" + customization.languageCode + "/html/" + group.infoFile;
            }

            bus.send("add-group", groupInfo);

			var items = group.items;
			for (var j = 0; j < items.length; j++) {
				var portalLayer = findById(layerRoot.portalLayers, items[j]);
				if (portalLayer != null) {
					var wmsLayer = findById(layerRoot.wmsLayers, portalLayer.layers[0]);
					if (wmsLayer != null) {
						var url = wmsLayer.baseUrl;
						var wmsName = wmsLayer.wmsName;
						var layerInfo = {
							"id" : portalLayer.id,
							"groupId" : group.id,
							"url" : url,
							"wmsName" : wmsName,
							"name" : portalLayer.label,
							"queryable" : wmsLayer.queryable,
							"visible" : getValueOrDefault(portalLayer, "active", false)
						};
						if (portalLayer.hasOwnProperty("infoFile")) {
							layerInfo.infoLink = "static/loc/" + customization.languageCode + "/html/" + portalLayer.infoFile;
						}
						if (wmsLayer.hasOwnProperty("wmsTime")) {
							layerInfo.timestamps = wmsLayer.wmsTime.split(",");
						}
						bus.send("add-layer", layerInfo);

                        // Set layer visibility on OpenLayers
                        bus.send("layer-visibility", [layerInfo.id, layerInfo.visible])
					} else {
						bus.send("error", "One (and only one) wms layer with id '" + id + "' expected");
					}

				} else {
					bus.send("error", "One (and only one) portal layer with id '" + id + "' expected");
				}
			}
		}

		bus.send("layers-loaded");
	});
});
