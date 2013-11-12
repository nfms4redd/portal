define([ "jquery" ], function($) {
	var map = $("<div/>").attr("id", "map").appendTo("body");
	var info = $("<div/>").attr("id", "info").appendTo("body");

	return {
		"mapId" : map.attr("id"),
		"infoId" : info.attr("id")
	}
});
