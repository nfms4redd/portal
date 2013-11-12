define([ "layout", "openlayers", "message-bus" ], function(layout, ol, bus) {

	var mapCRS = new OpenLayers.Projection("EPSG:900913");

	var map = new OpenLayers.Map("map", {
		theme : null,
		projection : mapCRS,
		units : "m",
		allOverlays : true,
		controls : []
	});
	map.addLayer(new OpenLayers.Layer.WMS("OpenLayers WMS", "http://vmap0.tiles.osgeo.org/wms/vmap0?", {
		layers : "basic"
	}));
	map.zoomToMaxExtent();

	bus.listen("layer", function(e, info) {
		var layer = new OpenLayers.Layer.WMS(name, info.url, {
			layers : info.name,
			transparent : true
		});
		map.addLayer(layer);
	});

	return map;
});