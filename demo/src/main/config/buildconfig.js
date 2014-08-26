({
	baseUrl : "${project.build.directory}/requirejs/nfms/modules",
	paths : {
		"jquery" : "../jslib/jquery-2.1.0",
		"jquery-ui" : "../jslib/jquery-ui-1.10.4.custom",
		"fancy-box": "../jslib/jquery.fancybox.pack",
		"openlayers": "../jslib/OpenLayers/OpenLayers.unredd",
		"mustache": "../jslib/jquery.mustache"
	},
	shim : {
		"fancy-box": [ "jquery" ],
		"mustache": [ "jquery" ]
	},
	out: "${basedir}/src/main/webapp/optimized/portal.js",
	name : "main",
	deps: [${scanFolder}],
})
