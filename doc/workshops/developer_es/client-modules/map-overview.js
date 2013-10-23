define([ "olmap" ], function(map) {
	var overviewLayer = new OpenLayers.Layer.WMS("OpenLayers WMS", "http://vmap0.tiles.osgeo.org/wms/vmap0?", {
		layers : "basic"
	});
	map.addControl(new OpenLayers.Control.OverviewMap({
		layers : [ overviewLayer ]
	}));
});