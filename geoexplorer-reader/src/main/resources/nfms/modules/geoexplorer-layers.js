define([ "message-bus", "module" ], function(bus, module) {

	var config = module.config();
	localGeoserverURL = config["local-geoserver-url"];
	if (localGeoserverURL == null) {
		console.error("Geoexplorer-layers plugin " + //
		"requires to configure the 'local-geoserver-url' " + //
		"parameter in plugin-conf.json: " + //
		"{'default-conf' : {\"geoexplorer-layers\" : {\"local-geoserver-url\" : \"http:...\"}}}");
	}

	bus.listen("modules-loaded", function() {
		bus.send("add-group", [ {
			id : "geoexplorer",
			name : "GeoExplorer"
		} ]);

		var layers = config["map"]["layers"];

		for (i in layers) {
			var layer = layers[i];
			if (layer["source"] == "local") {
				bus.send("add-layer", {
					"id" : "geoexplorer-" + i,
					"groupId" : "geoexplorer",
					"label" : layer["title"],
					"active" : layer["visibility"],
					"wmsLayers" : [ {
						"baseUrl" : localGeoserverURL + "/wms",
						"wmsName" : layer["name"]
					} ]
				});

			}
		}
	});

});