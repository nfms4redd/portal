define([ "jquery", "message-bus", "customization", "module" ], function($, bus, customization, module) {

	var defaultServer;
	var layerRoot;

	function findById(array, id) {
		return $.grep(array, function(l) {
			return l.id === id;
		});
	}

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

	function decorateCommons(groupOrPortalLayer) {
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
	function decorateGroup(parentId, group) {
		group["getParentId"] = function() {
			return parentId;
		}
		decorateCommons(group);
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

		decorateCommons(portalLayer);

		var layerInfoArray = [];
		if (!portalLayer.isPlaceholder()) {
			var mapLayerIds = (portalLayer.isPlaceholder()) ? null : portalLayer.layers;

			// Iterate over wms layers
			for (var j = 0; mapLayerIds != null && j < mapLayerIds.length; j++) {
				var mapLayers = findById(layerRoot.wmsLayers, mapLayerIds[j]);
				if (mapLayers.length === 0) {
					bus.send("error", "At least one layer with id '" + mapLayerIds[j] + "' expected");
					continue;
				}
				var mapLayer = mapLayers[0];
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
	}

	function decorateMapLayer(mapLayer, mapLayers) {
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

	function processGroup(parentId, group) {
		decorateGroup(parentId, group);
		bus.send("add-group", group);

		var items = group.items;

		for (var i = 0; i < items.length; i++) {
			var item = items[i];
			if (typeof item === 'object') {
				processGroup(group.getId(), item);
			} else {
				var portalLayers = findById(layerRoot.portalLayers, item);
				if (portalLayers.length !== 1) {
					bus.send("error", "One (and only one) portal layer with id '" + item + "' expected");
					continue;
				}

				var portalLayer = portalLayers[0];
				decoratePortalLayer(portalLayer, group.getId());

				bus.send("add-layer", portalLayer);
				bus.send("layer-visibility", [ portalLayer.id, portalLayer.active || false ]);
			}
		}
	}
	;

	var draw = function(newLayerRoot) {
		var i;
		layerRoot = newLayerRoot;
		defaultServer = null;
		if (newLayerRoot["default-server"]) {
			defaultServer = newLayerRoot["default-server"];
			defaultServer = $.trim(defaultServer);
			if (defaultServer.substring(0, 7) != "http://") {
				defaultServer = "http://" + defaultServer;
			}
		}
		var groups = newLayerRoot.groups;

		bus.send("before-adding-layers");

		for (i = 0; i < groups.length; i++) {
			processGroup(null, groups[i]);
		}

		bus.send("layers-loaded");
	};

	var redraw = function(newLayerRoot) {
		bus.send("reset-layers");
		draw(newLayerRoot);
	};

	bus.listen("modules-loaded", function() {
		draw(module.config());
	});

	bus.listen("decorate-and-add-layer", function(e, layerInfo, mapLayers, groupId) {
		if (!layerInfo.hasOwnProperty("layers")) {
			layerInfo["layers"] = [];
		}

		for (var i = 0; i < mapLayers.length; i++) {
			layerRoot.wmsLayers.push(mapLayers[i]);
			layerInfo["layers"].push(mapLayers[i].id);
		}
		layerRoot.portalLayers.push(layerInfo);
		var group = findById(layerRoot.groups, groupId);
		if (group.length == 0) {
			bus.send("error", "One (and only one) group with id '" + groupId + "' expected");
		} else {
			group[0].items.push(layerInfo.id);
		}

		redraw(layerRoot);
	});

	return {
		draw : draw,
		redraw : redraw
	};

});
