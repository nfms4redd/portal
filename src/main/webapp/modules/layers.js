define([ "jquery", "message-bus", "customization", "module" ], function($, bus, customization, module) {

		var findById = function(array, id) {
			return $.grep(array, function(l) {
				return l.id === id;
			});
		};

		bus.listen("add-portal-layer", function(event, portalLayer) {
			var wmsLayers = portalLayer.wmsLayers;
			var i;

			for (i = 0; i < wmsLayers.length; i++) {
				bus.send("add-layer", wmsLayers[i]);
			}
		})

		bus.listen("portal-layer-visibility", function(event, portalLayer) {
			var wmsLayers = portalLayer.wmsLayers;
			var i;

			for (i = 0; i < wmsLayers.length; i++) {
				bus.send("layer-visibility", [wmsLayers[i], portalLayer.active || false])
			}
		})

		var processGroup = function(layerRoot, parentId, group) {
			var items, item, portalLayers, portalLayer, wmsLayerIds,
				wmsLayers, wmsLayer, i, j, layerInfoArray;

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

		items = group.items;

		for (i = 0; i < items.length; i++) {
			item = items[i];
			if (typeof item === 'object') {
				processGroup(layerRoot, group.id, item);
			} else {
				portalLayers = findById(layerRoot.portalLayers, item);
				if (portalLayers.length !== 1) {
					bus.send("error", "One (and only one) portal layer with id '" + item + "' expected");
					continue;
				}

				portalLayer = portalLayers[0];
				wmsLayerIds = portalLayer.layers;

				layerInfoArray = [];

				// Iterate over wms layers
				for (j = 0; j < wmsLayerIds.length; j++) {
					wmsLayers = findById(layerRoot.wmsLayers, wmsLayerIds[j]);
					if (wmsLayers.length === 0) {
						bus.send("error", "At least one layer with id '" + wmsLayerIds[j] + "' expected");
						continue;
					}
					wmsLayer = wmsLayers[0];
					if (wmsLayer.hasOwnProperty("wmsTime")) {
						wmsLayer.timestamps = wmsLayer.wmsTime.split(",")
					}

					layerInfoArray.push(wmsLayer);
				}

				portalLayer.groupId = group.id
				portalLayer.wmsLayers = layerInfoArray;

				bus.send("add-portal-layer", portalLayer);
				bus.send("portal-layer-visibility", portalLayer);
			}
		}
	};


	bus.listen("modules-loaded", function() {
		var i;
		var layerRoot = module.config();
		var groups = layerRoot.groups;

		for (i = 0; i < groups.length; i++) {
			processGroup(layerRoot, null, groups[i]);
		}

		bus.send("layers-loaded");
	});

});
