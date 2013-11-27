define([ "map", "message-bus" ], function(map, bus) {

	var initialZoom = function() {
		var epsg4326 = new OpenLayers.Projection("EPSG:4326");
		var center = new OpenLayers.LonLat(-84, 0);
		center.transform(epsg4326, map.projection);
		map.setCenter(center, 6);
	};

	bus.listen("layers-loaded", initialZoom);
	bus.listen("initial-zoom", initialZoom);
});