require.config({
	baseUrl : "modules",
	// uncomment this line for debugging purposes in order to bust cache
	urlArgs : "bust=" + (new Date()).getTime(),
	paths : {
		"jquery" : "./js/jquery-2.1.0.js",
		"jquery-ui" : "./js/jquery-ui-1.10.3.js",
		"fancy-box" : "../js/jquery.fancybox.pack",
		"openlayers" : "../js/OpenLayers/OpenLayers.debug",
		"mustache" : "../js/jquery.mustache"
	},
	shim : {
		"fancy-box" : [ "jquery" ],
		"mustache" : [ "jquery" ]
	}
});

require([ "customization" ]);
