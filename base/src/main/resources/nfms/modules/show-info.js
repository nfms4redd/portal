define([ "message-bus", "jquery", "fancy-box" ], function(bus, $) {

	bus.listen("show-info", function(event, title, link, eventOptions) {
		var defaultOptions = {
			"openEffect" : "elastic",
			"closeEffect" : "elastic",
			"overlayOpacity" : 0.5,
			"title" : title
		};
		if ($.type(link) === "string") {
			defaultOptions["type"]= "iframe";
			defaultOptions["href"]= link;
		} else {
			defaultOptions["type"]= "html";
			defaultOptions["content"]= link;
		}
		if (eventOptions) {
			$.extend(defaultOptions, eventOptions);
		}
		$.fancybox.open([ defaultOptions ]);
	});

	bus.listen("hide-info", function() {
		$.fancybox.close();
	});

});