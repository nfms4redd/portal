define([ "layout", "openlayers" ], function(layout, ol) {

	var mapCRS = new OpenLayers.Projection("EPSG:900913");

	var map = new OpenLayers.Map(layout.mapId, {
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

});