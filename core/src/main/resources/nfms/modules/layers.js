define([ "jquery", "message-bus", "customization", "module" ], function($, bus, customization, module) {

	var defaultServer;
	var layerRoot;

	var getGetLegendGraphicUrl = function(wmsLayer) {
		var url = wmsLayer.baseUrl;
		if (url.indexOf("?") === -1) {
			url = url + "?";
		} else {
			url = url + "$";
		}
		url += "REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&TRANSPARENT=true&LAYER=";
		url += wmsLayer.wmsName;

		return url;
	}

	var checkMandatoryParameter = function(wmsLayer, propertyName) {
		if (!wmsLayer.hasOwnProperty(propertyName)) {
			bus.send("error", propertyName + " mandatory when queryType='wfs', in layer: " + wmsLayer["id"]);
		}
	}

	function decorateCommons(o) {
		o["merge"] = function(data) {
			$.extend(o, data);

			draw();
		}
	}

	function decorateCommonsPortalLayerOrGroup(groupOrPortalLayer) {
		decorateCommons(groupOrPortalLayer);
		groupOrPortalLayer["getName"] = function() {
			return groupOrPortalLayer.label;
		}
		groupOrPortalLayer["getId"] = function() {
			return groupOrPortalLayer.id;
		}
		groupOrPortalLayer["getInfoLink"] = function() {
			var ret = null;
			if (groupOrPortalLayer.hasOwnProperty("infoLink")) {
				ret = groupOrPortalLayer.infoLink;
			} else if (groupOrPortalLayer.hasOwnProperty("infoFile")) {
				ret = "static/loc/" + customization.languageCode + "/html/" + groupOrPortalLayer.infoFile;
			}

			return ret;
		}
	}

	function deleteAllGroupLayers(group) {
		for (var i = 0; i < group["items"].length; i++) {
			var groupItem = group["items"][i];
			if (typeof groupItem === 'string' || groupItem instanceof String) {
				doDeleteLayer(groupItem);
			} else if (groupItem["items"]) {
				deleteAllGroupLayers(groupItem);
			}
		}
	}

	function findAndDeleteGroup(array, groupId) {
		// Directly in the array
		for (var i = 0; i < array.length; i++) {
			if (array[i]["id"] == groupId) {
				deleteAllGroupLayers(array[i]);
				array.splice(i, 1);
				return true;
			}
		}

		// Delegate on each group
		for (var i = 0; i < array.length; i++) {
			if (array[i].hasOwnProperty("items")) {
				if (findAndDeleteGroup(array[i]["items"], groupId)) {
					return true;
				}
			}
		}

		return false;
	}

	function decorateGroup(parentId, group) {
		group["getParentId"] = function() {
			return parentId;
		}
		group["getParent"] = function() {
			if (parentId == null) {
				return null;
			} else {
				return layerRoot.getGroup(parentId);
			}
		}
		decorateCommonsPortalLayerOrGroup(group);

		group["remove"] = function() {
			findAndDeleteGroup(layerRoot.groups, group.getId());
			draw();
		}
	}

	function doDeleteLayer(layerId) {
		var portalLayerRemovalFunction = function(testPortalLayer, i) {
			if (testPortalLayer.getId() == layerId) {
				layerRoot.portalLayers.splice(i, 1);

				if (testPortalLayer["layers"]) {
					var wmsLayerRemovalFunction = function(testWMSLayer, k) {
						if (testWMSLayer.getId() == wmsLayerId) {
							layerRoot.wmsLayers.splice(k, 1);
							return true;
						}

						return false;
					};
					for (var j = 0; j < testPortalLayer.layers.length; j++) {
						var wmsLayerId = testPortalLayer.layers[j];
						process(layerRoot.wmsLayers, wmsLayerRemovalFunction);
					}
				}

				return true;
			}

			return false;
		};
		process(layerRoot.portalLayers, portalLayerRemovalFunction);
	}

	function decoratePortalLayer(portalLayer, groupId) {
		portalLayer["isPlaceholder"] = function() {
			return (portalLayer.layers === undefined) || (portalLayer.layers.length === 0);
		}
		portalLayer["getTimestamps"] = function() {
			var ret = null;
			if (portalLayer.hasOwnProperty("timeInstances")) {
				ret = portalLayer.timeInstances.split(",");
			}

			return ret;
		}

		decorateCommonsPortalLayerOrGroup(portalLayer);

		var layerInfoArray = [];
		if (!portalLayer.isPlaceholder()) {
			var mapLayerIds = (portalLayer.isPlaceholder()) ? null : portalLayer.layers;

			// Iterate over wms layers
			for (var j = 0; mapLayerIds != null && j < mapLayerIds.length; j++) {
				var mapLayer = findById(layerRoot.wmsLayers, mapLayerIds[j]);
				if (mapLayer == null) {
					bus.send("error", "Map layer '" + mapLayerIds[j] + "' not found");
					continue;
				}
				decorateMapLayer(mapLayer, layerRoot.wmsLayers);

				layerInfoArray.push(mapLayer);
			}
		}
		portalLayer["getMapLayers"] = function() {
			return layerInfoArray;
		}
		portalLayer["isActive"] = function() {
			return portalLayer["active"];
		}
		portalLayer["isFeedbackEnabled"] = function() {
			return portalLayer["feedback"];
		}
		portalLayer["getGroupId"] = function() {
			return groupId;
		}
		portalLayer["hasTimeDependentStyle"] = function() {
			return portalLayer.hasOwnProperty("timeStyles");
		}
		portalLayer["getTimeStyles"] = function() {
			return portalLayer["timeStyles"];
		}
		portalLayer["getDateFormat"] = function() {
			return portalLayer["date-format"];
		}
		portalLayer["getInlineLegendURL"] = function() {
			var ret = null;
			if (portalLayer["inlineLegendUrl"]) {
				if (portalLayer["inlineLegendUrl"] == "auto") {
					var firstMapLayer = portalLayer.getMapLayers()[0];
					ret = getGetLegendGraphicUrl(firstMapLayer);
				} else if (portalLayer["inlineLegendUrl"].charAt(0) == "/") {
					ret = defaultServer + portalLayer["inlineLegendUrl"];
				}
			}

			return ret;
		}
		portalLayer["remove"] = function() {
			doDeleteLayer(portalLayer.getId());

			process(layerRoot.groups, function(group, i) {
				if (group.hasOwnProperty("items")) {
					var index = group["items"].indexOf(portalLayer.getId());
					if (index != -1) {
						group["items"].splice(index, 1);
						return true;
					}
				}

				return false;
			});

			draw();
		}
	}

	function decorateMapLayer(mapLayer, mapLayers) {
		decorateCommons(mapLayer);
		mapLayer["getId"] = function() {
			return mapLayer["id"];
		}
		mapLayer["getName"] = function() {
			return mapLayer["label"];
		}
		mapLayer["getZIndex"] = function() {
			return mapLayers.indexOf(mapLayer);
		}
		mapLayer["getBaseURL"] = function() {
			var ret = mapLayer["baseUrl"];
			if (mapLayer["baseUrl"]) {
				if (mapLayer["baseUrl"].charAt(0) == "/") {
					ret = defaultServer + ret;
				}
			}

			return ret;
		}
		mapLayer["getServerLayerName"] = function() {
			return mapLayer["wmsName"];
		}
		mapLayer["getImageFormat"] = function() {
			return mapLayer["imageFormat"];
		}
		mapLayer["getType"] = function() {
			return mapLayer["type"];
		}
		mapLayer["getOSMURLs"] = function() {
			return mapLayer["osmUrls"];
		}
		mapLayer["getGMapsType"] = function() {
			return mapLayer["gmaps-type"];
		}
		mapLayer["getLegendURL"] = function() {
			var ret = null;
			if (mapLayer.hasOwnProperty("legend")) {
				if (mapLayer.legend == "auto") {
					ret = getGetLegendGraphicUrl(mapLayer);
				} else {
					ret = "static/loc/" + customization.languageCode + "/images/" + mapLayer.legend;
				}
			}
			return ret;
		}

		mapLayer["getSourceLink"] = function() {
			return mapLayer["sourceLink"];
		}

		mapLayer["getSourceLabel"] = function() {
			return mapLayer["sourceLabel"];
		}
		mapLayer["isQueryable"] = function() {
			return mapLayer.hasOwnProperty("queryType");
		}
		mapLayer["queriesWFS"] = function() {
			return mapLayer["queryType"] == "wfs";
		}
		mapLayer["getQueryGeomFieldName"] = function() {
			return mapLayer["queryGeomFieldName"];
		}
		mapLayer["getQueryTimeFieldName"] = function() {
			return mapLayer["queryTimeFieldName"];
		}
		mapLayer["highlightBounds"] = function() {
			return mapLayer.hasOwnProperty("queryHighlightBounds") && mapLayer["queryHighlightBounds"];
		}
		mapLayer["getQueryURL"] = function() {
			var queryURL = null;
			if (mapLayer.hasOwnProperty("queryUrl")) {
				queryURL = mapLayer["queryUrl"];
			} else {
				queryURL = mapLayer["baseUrl"];
			}

			return queryURL;
		}
		mapLayer["getQueryFieldNames"] = function() {
			return mapLayer["queryFieldNames"];
		}
		mapLayer["getQueryFieldAliases"] = function() {
			return mapLayer["queryFieldAliases"];
		}

		// Check info parameters
		if (mapLayer.hasOwnProperty("queryType") && mapLayer["queryType"] == "wfs") {
			checkMandatoryParameter(mapLayer, "queryGeomFieldName");
			checkMandatoryParameter(mapLayer, "queryFieldNames");
			checkMandatoryParameter(mapLayer, "queryFieldAliases");
		}

	}

	function process(array, processFunction) {
		for (var i = 0; i < array.length; i++) {
			if (processFunction(array[i], i)) {
				break;
			}
			if (array[i].hasOwnProperty("items")) {
				process(array[i]["items"], processFunction);
			}
		}
	}

	function findById(array, id) {
		var ret = null;
		process(array, function(o) {
			if (o["id"] == id) {
				ret = o;

				return true;
			}

			return false;
		});

		return ret;
	}

	function decorateLayerRoot(layerRoot) {
		layerRoot["getPortalLayer"] = function(layerId) {
			return findById(layerRoot.portalLayers, layerId);
		}
		layerRoot["getWMSLayer"] = function(layerId) {
			return findById(layerRoot.wmsLayers, layerId);
		}
		layerRoot["getGroup"] = function(groupId) {
			return findById(layerRoot.groups, groupId);
		}
		layerRoot["getDefaultServer"] = function() {
			return layerRoot["default-server"];
		}
		layerRoot["addLayer"] = function(groupId, portalLayer, wmsLayer) {
			var group = findById(layerRoot.groups, groupId);
			group.items.push(portalLayer.id);

			layerRoot.wmsLayers.push(wmsLayer);
			decorateMapLayer(wmsLayer);

			layerRoot.portalLayers.push(portalLayer);
			decoratePortalLayer(portalLayer);

			draw();
		}
		layerRoot["addGroup"] = function(group) {
			layerRoot.groups.push(group);
			decorateGroup(null, group);

			draw();
		}
		layerRoot["moveGroup"] = function(groupId, parentId, newPosition) {
			var group = layerRoot.getGroup(groupId);

			// delete
			var sourceItemsArray = layerRoot.groups;
			var parentGroup = group.getParent();
			if (parentGroup != null) {
				sourceItemsArray = parentGroup["items"];
			}
			var sourceIndex = -1;
			for (var i = 0; i < sourceItemsArray.length; i++) {
				if (sourceItemsArray[i].getId() == groupId) {
					sourceIndex = i;
					break;
				}
			}
			var sameGroup = (group.getParentId() == null && parentId == null) || group.getParentId() == parentId;
			if (!sameGroup || sourceIndex != newPosition) {
				sourceItemsArray.splice(sourceIndex, 1);

				// insert
				var itemsArray = layerRoot.groups;
				if (parentId != null) {
					itemsArray = layerRoot.getGroup(parentId)["items"];
				}
				itemsArray.splice(newPosition, 0, group);

				draw();
			}
		}
		layerRoot["moveLayer"] = function(layerId, parentId, newPosition) {
			var groupId = layerRoot.getPortalLayer(layerId).getGroupId();
			var group = layerRoot.getGroup(groupId);

			// delete
			var sourceItemsArray = group["items"];
			var sourceIndex = -1;
			for (var i = 0; i < sourceItemsArray.length; i++) {
				if (sourceItemsArray[i] == layerId) {
					sourceIndex = i;
					break;
				}
			}
			if (parentId != groupId || newPosition != sourceIndex) {
				sourceItemsArray.splice(sourceIndex, 1);

				// insert
				var targetGroup = layerRoot.getGroup(parentId);
				var itemsArray = targetGroup["items"];
				itemsArray.splice(newPosition, 0, layerId);

				draw();
			}
		}
	}

	function processGroup(parentId, group) {
		decorateGroup(parentId, group);
		bus.send("add-group", group);

		var items = group.items;

		for (var i = 0; i < items.length; i++) {
			var item = items[i];
			if (typeof item === 'object') {
				processGroup(group.getId(), item);
			} else {
				var portalLayer = findById(layerRoot.portalLayers, item);
				if (portalLayer == null) {
					bus.send("error", "Portal layer with id '" + item + "' not found");
					continue;
				}

				decoratePortalLayer(portalLayer, group.getId());

				bus.send("add-layer", portalLayer);
				bus.send("layer-visibility", [ portalLayer.id, portalLayer.active || false ]);
			}
		}
	}

	var draw = function() {
		bus.send("reset-layers");
		var i;
		defaultServer = null;
		if (layerRoot["default-server"]) {
			defaultServer = layerRoot["default-server"];
			defaultServer = $.trim(defaultServer);
			if (defaultServer.substring(0, 7) != "http://") {
				defaultServer = "http://" + defaultServer;
			}
		}
		var groups = layerRoot.groups;

		bus.send("before-adding-layers");

		for (i = 0; i < groups.length; i++) {
			processGroup(null, groups[i]);
		}
		decorateLayerRoot(layerRoot);

		bus.send("layers-loaded", [ layerRoot ]);
	};

	var redraw = function(newLayerRoot) {
		layerRoot = newLayerRoot;
		draw();
	};

	function getLayerRoot() {
		return layerRoot;
	}

	bus.listen("modules-loaded", function() {
		layerRoot = module.config();
		draw();
	});

	bus.listen("decorate-and-add-layer", function(e, layerInfo, mapLayer, groupId) {
		if (!layerInfo.hasOwnProperty("layers")) {
			layerInfo["layers"] = [ mapLayer.id ];
		}
		layerRoot.addLayer(groupId, layerInfo, mapLayer);
	});

	bus.listen("save-layers", function() {
		bus.send("ajax", {
			type : 'PUT',
			url : 'layers.json',
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(layerRoot, null, 4),
			success : function(data, textStatus, jqXHR) {
				require([ "text!../layers.json?a=" + new Date().getTime() ], function(newLayerRott) {
					layerRoot = JSON.parse(newLayerRott);
					draw();
				});
			},
			errorMsg : "Error uploading layers.json to the server" // TODO i18n
		});
	});

});
