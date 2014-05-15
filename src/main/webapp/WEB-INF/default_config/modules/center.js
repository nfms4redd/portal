define([ "map", "message-bus", "customization" ], function(map, bus, customization) {

	var initialZoom = function() {
		var epsg4326 = new OpenLayers.Projection("EPSG:4326");
		var center = new OpenLayers.LonLat(customization["map.centerLonLat"]);
		center.transform(epsg4326, map.projection);
		map.setCenter(center, customization["map.initialZoomLevel"]);
	};

	bus.listen("layers-loaded", initialZoom);
	bus.listen("initial-zoom", initialZoom);
});