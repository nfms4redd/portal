define([ "message-bus", "jquery", "fancy-box" ], function(bus, $) {

	bus.listen("show-info", function(event, title, link) {
		$.fancybox.open([ {
			"href" : link,
			"closeBtn" : "true",
			"openEffect" : "elastic",
			"closeEffect" : "elastic",
			"type" : "iframe",
			"overlayOpacity" : 0.5,
			"title" : title
		} ]);
	});

	bus.listen("hide-info", function() {
		$.fancybox.close();
	});

});