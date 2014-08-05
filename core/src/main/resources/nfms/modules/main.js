require.config({
	baseUrl : "modules",
	// uncomment this line for debugging purposes in order to bust cache
	urlArgs : "bust=" + (new Date()).getTime(),
	paths : {
		"jquery" : "http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min",
		"jquery-ui" : "http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min",
		"fancy-box" : "../jslib/jquery.fancybox.pack",
		"openlayers" : "../jslib/OpenLayers/OpenLayers.debug",
		"mustache" : "../jslib/jquery.mustache"
	},
	shim : {
		"fancy-box" : [ "jquery" ],
		"mustache" : [ "jquery" ]
	}
});

require([ "customization" ]);
