define([ "message-bus" ], function(bus) {

	var idLinkInfo = {};

	bus.listen("before-adding-layers", function() {

		var showInfoAction = function(portalLayer) {
			if (portalLayer.hasOwnProperty("infoLink")) {
				aLink = $("<a/>");
				aLink.addClass("layer_info_button");
				aLink.attr("id", "layer_info_button_" + portalLayer.id);
				aLink.click(function() {
					bus.send("show-layer-info", [ portalLayer.id ]);
				});
				return aLink;
			} else {
				return null;
			}
		};

		bus.send("register-layer-action", showInfoAction);
	});

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.hasOwnProperty("infoLink")) {
			idLinkInfo[layerInfo.id] = {
				"link" : layerInfo.infoLink,
				"title" : layerInfo.name
			}
		}
	});

	bus.listen("show-layer-info", function(event, layerId) {
		if (idLinkInfo.hasOwnProperty(layerId)) {
			var linkInfo = idLinkInfo[layerId];
			bus.send("show-info", [linkInfo.title, linkInfo.link]);
		}
	});
});