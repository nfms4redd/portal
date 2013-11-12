define([ "openlayers", "map" ], function(ol, map) {
	
	var epsg4326 = new OpenLayers.Projection("EPSG:4326");
	
	map.setCenter(new OpenLayers.LonLat(-84, 0).transform(epsg4326, map.projection), 5);
});