define([ "jquery", "message-bus", "customization", "module" ], function($, bus, customization, module) {

	var findById = function(array, id) {
		return $.grep(array, function(l) {
			return l.id === id;
		});
	};

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
				
				// CHECK IF the CURRENT portalLayer is a PLACEHOLDER
				// if no layers(wmsLayers) are defined in portalLayers means that the portalLayer
				// is just a placeholder in the layer menu, used to store generic info 
				portalLayer.isPlaceholder = (portalLayer.layers === undefined) || (portalLayer.layers.length === 0); 

				if (portalLayer.hasOwnProperty("infoFile")) {
					portalLayer.infoLink = "static/loc/" + customization.languageCode + "/html/" + portalLayer.infoFile;
				}
				if (portalLayer.hasOwnProperty("timeInstances")) {
					portalLayer.timestamps = portalLayer.timeInstances.split(",")
				}

				wmsLayerIds = (portalLayer.isPlaceholder)?null:portalLayer.layers;

				layerInfoArray = [];

				// Iterate over wms layers 
				for (j = 0; !portalLayer.isPlaceholder && j < wmsLayerIds.length; j++) {
					wmsLayers = findById(layerRoot.wmsLayers, wmsLayerIds[j]);
					if (wmsLayers.length === 0) {
						bus.send("error", "At least one layer with id '" + wmsLayerIds[j] + "' expected");
						continue;
					}
					wmsLayer = wmsLayers[0];
                    wmsLayer.zIndex = layerRoot.wmsLayers.indexOf(wmsLayer);
					layerInfoArray.push(wmsLayer);
				}

				portalLayer.groupId = group.id
				portalLayer.wmsLayers = layerInfoArray;

				bus.send("add-layer", portalLayer);
				bus.send("layer-visibility", [ portalLayer.id, portalLayer.active || false ]);
			}
		}
	};


	bus.listen("modules-loaded", function() {
		var i;
		var layerRoot = module.config();
		var groups = layerRoot.groups;

		bus.send("before-adding-layers");
		
		for (i = 0; i < groups.length; i++) {
			processGroup(layerRoot, null, groups[i]);
		}

		bus.send("layers-loaded");
	});

});
