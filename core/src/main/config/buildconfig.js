({
	baseUrl : "${basedir}/src/main/webapp/modules",
	paths : {
		"jquery" : "../js/jquery-2.1.0",
		"jquery-ui" : "../js/jquery-ui-1.10.4.custom",
		"fancy-box": "../js/jquery.fancybox.pack",
		"openlayers": "../js/OpenLayers/OpenLayers.unredd",
		"mustache": "../js/jquery.mustache"
	},
	shim : {
		"fancy-box": [ "jquery" ],
		"mustache": [ "jquery" ]
	},
	name: "main-optimizer",
	out: "${basedir}/src/main/webapp/js/min/portal.js",
	deps: ["layers", "communication", "iso8601", "error-management", "map", "banner", "toolbar", "time-slider",
		"layer-list", "info-control", "info-dialog", "center", "zoom-bar", "layer-list-selector", "active-layer-list"],
//	uglify: {
//		toplevel: true,
//		ascii_only: true,
//		beautify: true,
//		max_line_length: 100,
//
//		//How to pass uglifyjs defined symbols for AST symbol replacement,
//		//see "defines" options for ast_mangle in the uglifys docs.
//		defines: {
//			DEBUG: ['name', 'false']
//		},
//
//		//Custom value supported by r.js but done differently
//		//in uglifyjs directly:
//		//Skip the processor.ast_mangle() part of the uglify call (r.js 2.0.5+)
//		no_mangle: true
//	}
})
