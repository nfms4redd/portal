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

	var processGroup = function(layerRoot, parentId, group) {
		var groupInfo = {
			"id" : group.id,
			"name" : group.label
		};
		if (group.hasOwnProperty("infoFile")) {
			groupInfo.infoLink = "static/loc/" + customization.languageCode + "/html/" + group.infoFile;
		}
		if (parentId !== null) {
			groupInfo.parentId = parentId;
		}

		bus.send("add-group", groupInfo);

		var items = group.items;
		for (var j = 0; j < items.length; j++) {
			var item = items[j];
			if (typeof item === 'object') {
				processGroup(layerRoot, group.id, item);
			} else {
				var portalLayer = findById(layerRoot.portalLayers, item);
				if (portalLayer != null) {
					var wmsLayerId = portalLayer.layers[0];
					var wmsLayer = findById(layerRoot.wmsLayers, wmsLayerId);
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
                        bus.send("layer-visibility", [layerInfo, layerInfo.visible])
					} else {
						bus.send("error", "One (and only one) wms layer with id '" + wmsLayerId + "' expected");
					}
				} else {
					bus.send("error", "One (and only one) portal layer with id '" + item + "' expected");
				}
			}
		}
	};
	
	bus.listen("modules-loaded", function() {
		var layerRoot = module.config();
		var groups = layerRoot.groups;
		for (var i = 0; i < groups.length; i++) {
			processGroup(layerRoot, null, groups[i]);
		}

		bus.send("layers-loaded");
	});
});
