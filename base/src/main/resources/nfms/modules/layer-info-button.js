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

	bus.listen("reset-layers", function() {
		idLinkInfo = {};
	});

	bus.listen("before-adding-layers", function() {

		var showInfoLayerAction = function(portalLayer) {
			if (portalLayer.getInfoLink() != null) {
				return buildLink(portalLayer.getId(), "show-layer-info");
			} else {
				return null;
			}
		};
		var showInfoGroupAction = function(group) {
			if (group.getInfoLink() != null) {
				return buildLink(group.getId(), "show-group-info");
			} else {
				return null;
			}
		};

		bus.send("register-layer-action", showInfoLayerAction);
		bus.send("register-group-action", showInfoGroupAction);
	});

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.getInfoLink() != null) {
			idLinkInfo["layer-" + layerInfo.getId()] = {
				"link" : layerInfo.getInfoLink(),
				"title" : layerInfo.getName()
			}
		}
	});

	bus.listen("add-group", function(event, groupInfo) {
		if (groupInfo.getInfoLink() != null) {
			idLinkInfo["group-" + groupInfo.getId()] = {
				"link" : groupInfo.getInfoLink(),
				"title" : groupInfo.getName()
			}
		}
	});

	var showInfo = function(id) {
		if (idLinkInfo.hasOwnProperty(id)) {
			var linkInfo = idLinkInfo[id];
			bus.send("show-info", [ linkInfo.title, linkInfo.link ]);
		}
	}

	bus.listen("show-layer-info", function(event, layerId) {
		showInfo("layer-" + layerId);
	});

	bus.listen("show-group-info", function(event, groupId) {
		showInfo("group-" + groupId);
	});
});