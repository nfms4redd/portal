define([ "message-bus", "jquery", "fancy-box" ], function(bus, $) {

	bus.listen("show-info", function(event, title, link, eventOptions) {
		var defaultOptions = {
			"href" : link,
			"openEffect" : "elastic",
			"closeEffect" : "elastic",
			"type" : "iframe",
			"overlayOpacity" : 0.5,
			"title" : title
		};
		if (eventOptions) {
			$.extend(defaultOptions, eventOptions);
		}
		$.fancybox.open([ defaultOptions ]);
	});

	bus.listen("hide-info", function() {
		$.fancybox.close();
	});

});