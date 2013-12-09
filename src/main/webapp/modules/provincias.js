define([ "message-bus", "info-dialog" ], function(bus) {
	var renderer = function(feature) {
		return $("<div>This is a province, boh! " + feature.attributes.PROVINCE + "</div>");
	};

	bus.send("add-layer-info-renderer", [ "unredd:drc_provinces", renderer ]);
});