define([ "map", "message-bus" ], function(map, bus) {

	bus.listen("layers-loaded", function() {
		var epsg4326 = new OpenLayers.Projection("EPSG:4326");
		var center = new OpenLayers.LonLat(-84, 0);
		center.transform(epsg4326, map.projection);
		map.setCenter(center, 6);
	});
});