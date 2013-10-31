define([ "jquery" ], function($) {
	var layerList = $("<div/>").css({
		float : "right"
	}).prependTo("body");
	var ul = $("<ul/>").appendTo(layerList);

	return {
		addLayer : function(name) {
			ul.append($("<li/>").html(name));
		}
	}
});