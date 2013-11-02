define([ "jquery", "message-bus", "module" ], function($, bus, module) {

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
	var layerRoot = module.config();
	var groups = layerRoot.groups;
	for ( var i = 0; i < groups.length; i++) {
		var group = groups[i];
		bus.send("add-group", {
			"id" : group.id,
			"name" : group.label
		});

		var items = group.items;
		for ( var j = 0; j < items.length; j++) {
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
						"infoLink" : "http://rdc-snsf.org/static/loc/en/html/bluemarble_def.html",
						"queryable" : wmsLayer.queryable,
						"visible" : getValueOrDefault(portalLayer, "active", true)
					};
					if (wmsLayer.hasOwnProperty("wmsTime")) {
						layerInfo.timestamps = wmsLayer.wmsTime.split(",");
					}
					bus.send("add-layer", layerInfo);
				} else {
					bus.send("trigger", "error", "One (and only one) wms layer with id '" + id + "' expected");
				}

			} else {
				bus.send("trigger", "error", "One (and only one) portal layer with id '" + id + "' expected");
			}
		}

		bus.send("initial-zoom");
	}
});
