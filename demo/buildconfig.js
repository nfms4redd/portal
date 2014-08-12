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
//	deps: ["layers", "communication", "iso8601", "error-management", "map", "banner", "toolbar", "time-slider",
//		"layer-list", "info-control", "info-dialog", "center", "zoom-bar", "layer-list-selector", "active-layer-list"],
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
