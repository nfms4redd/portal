define([ "map", "message-bus", "customization", "url-parameters" ], function(map, bus, customization, urlParams) {

	var initialZoom = function() {
		var epsg4326 = new OpenLayers.Projection("EPSG:4326");

		var center;
		var urlCenter = urlParams.get("map.centerLonLat");
		if (urlCenter != null) {
			center = new OpenLayers.LonLat(urlCenter.split(","));
		} else {
			center = new OpenLayers.LonLat(customization["map.centerLonLat"]);
		}
		center.transform(epsg4326, map.projection);
		var zoomLevel = urlParams.get("map.initialZoomLevel");
		if (zoomLevel == null) {
			zoomLevel = customization["map.initialZoomLevel"];
		}
		map.setCenter(center, zoomLevel);
	};

	bus.listen("layers-loaded", initialZoom);
	bus.listen("initial-zoom", initialZoom);
});