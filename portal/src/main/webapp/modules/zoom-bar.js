define([ "jquery", "message-bus" ], function($, bus) {

	var btnZoomOut = $("<a/>").attr("id", "zoom_out").appendTo("body");
	btnZoomOut.click(function() {
		bus.send("zoom-out");
	});
	var btnZoomIn = $("<a/>").attr("id", "zoom_in").appendTo("body");
	btnZoomIn.click(function() {
		bus.send("zoom-in");
	});
	var btnZoomFull = $("<a/>").attr("id", "zoom_to_max_extent").appendTo("body");
	btnZoomFull.click(function() {
		bus.send("initial-zoom");
	});
});
