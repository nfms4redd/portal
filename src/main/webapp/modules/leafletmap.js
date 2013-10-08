define([ "leaflet" ], function(L) {
	$(document).bind(
			"init-map",
			function(event, div) {
				$(document).trigger(
						"css-load",
						"http://cdn.leafletjs.com/"
								+ "leaflet-0.6.4/leaflet.css");

				var divMap = $("<div/>").css("width", "100%").css("height",
						"100%").attr("id", "map");
				div.append(divMap);
				map = L.map('map', {
					crs : L.CRS.EPSG4326
				}).setView([ 51.505, -0.09 ], 10);
				new L.TileLayer.WMS("http://vmap0.tiles.osgeo.org/wms/vmap0", {
					layers : 'basic',
					format : 'image/png',
					transparent : true
				}).addTo(map);
			});

	$(document).bind("add-layer", function(event, url, layerName) {
		if (map !== null) {
			new L.TileLayer.WMS(url, {
				layers : layerName,
				format : 'image/png',
				transparent : true
			}).addTo(map);
		}
	});

});