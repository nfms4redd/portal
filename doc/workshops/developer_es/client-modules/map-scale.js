define([ "map", "message-bus" ], function(map, bus) {
	bus.send("css-load", "modules/map-scale.css");

	map.addControl(new OpenLayers.Control.Scale());
});