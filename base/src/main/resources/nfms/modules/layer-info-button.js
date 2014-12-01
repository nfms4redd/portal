define([ "message-bus", "fancy-box" ], function(bus) {

	bus.listen("before-adding-layers", function() {

		var showInfoAction = function(portalLayer) {
			if (portalLayer.hasOwnProperty("infoFile")) {
				aLink = $("<a/>").attr("href", portalLayer.infoFile);
				aLink.addClass("layer_info_button");
				aLink.fancybox({
					"closeBtn" : "true",
					"openEffect" : "elastic",
					"closeEffect" : "elastic",
					"type" : "iframe",
					"overlayOpacity" : 0.5
				});
				return aLink;
			} else {
				return null;
			}
		};

		bus.send("register-layer-action", showInfoAction);

	});
});