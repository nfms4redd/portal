define([ "jquery", "message-bus" ], function($, bus) {
	var layerList = $("<div/>").css({
		float : "right"
	}).prependTo("body");
	var ul = $("<ul/>").appendTo(layerList);

	bus.listen("layer", function(e, info) {
		ul.append($("<li/>").html(info.name));
	});
});