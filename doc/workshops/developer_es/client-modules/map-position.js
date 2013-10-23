define([ "olmap", "i18n" ], function(map, i18n) {
	map.addControl(new OpenLayers.Control.MousePosition({
		prefix : "<a target='_blank' " + //
		"href='http://spatialreference.org/ref/epsg/4326/'>" + // 
		"EPSG:4326</a> " + i18n.coordinates + ": ",
		separator : ' | ',
		numDigits : 2,
		emptyString : ''
	}));
});
