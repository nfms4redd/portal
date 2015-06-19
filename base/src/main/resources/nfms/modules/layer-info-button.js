define([ "message-bus" ], function(bus) {

	var idLinkInfo = {};

	var buildLink = function(id, eventName) {
		aLink = $("<a/>");
		aLink.addClass("layer_info_button");
		aLink.attr("id", "layer_info_button_" + id);
		aLink.click(function() {
			bus.send(eventName, [ id ]);
		});
		return aLink;
	}
	
	bus.listen("before-adding-layers", function() {
		
		var showInfoLayerAction = function(portalLayer) {
			if (portalLayer.hasOwnProperty("infoLink")) {
				return buildLink(portalLayer.id, "show-layer-info");
			} else {
				return null;
			}
		};
		var showInfoGroupAction = function(group) {
			if (group.hasOwnProperty("infoLink")) {
				return buildLink(group.id, "show-group-info");
			} else {
				return null;
			}
		};

		bus.send("register-layer-action", showInfoLayerAction);
		bus.send("register-group-action", showInfoGroupAction);
	});

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.hasOwnProperty("infoLink")) {
			idLinkInfo["layer-" + layerInfo.id] = {
				"link" : layerInfo.infoLink,
				"title" : layerInfo.name
			}
		}
	});

	bus.listen("add-group", function(event, groupInfo) {
		if (groupInfo.hasOwnProperty("infoLink")) {
			idLinkInfo["group-" + groupInfo.id] = {
				"link" : groupInfo.infoLink,
				"title" : groupInfo.name
			}
		}
	});

	var showInfo = function(id) {
		if (idLinkInfo.hasOwnProperty(id)) {
			var linkInfo = idLinkInfo[id];
			bus.send("show-info", [linkInfo.title, linkInfo.link]);
		}
	}
	
	bus.listen("show-layer-info", function(event, layerId) {
		showInfo("layer-" + layerId);
	});

	bus.listen("show-group-info", function(event, groupId) {
		showInfo("group-" + groupId);
	});
});