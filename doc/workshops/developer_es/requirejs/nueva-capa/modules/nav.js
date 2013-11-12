define([ "openlayers", "map" ], function(ol, map) {
	var navControl = new OpenLayers.Control.Navigation();

	map.addControl(navControl);

});