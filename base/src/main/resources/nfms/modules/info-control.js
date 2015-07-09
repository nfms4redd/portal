define([ "map", "message-bus", "customization" ], function(map, bus, customization) {

	// Associates wmsLayers with controls
	var layerIdControl = {};

	// Associates wmsLayers with the selected timestamps
	var layerIdTimestamp = {};

	// List of controls
	var controls = [];

	bus.listen("add-layer", function(e, layerInfo) {
		var wmsLayers = layerInfo.wmsLayers;
		for (var i = 0; i < wmsLayers.length; i++) {
			var wmsLayer = wmsLayers[i];
			if (!wmsLayer.hasOwnProperty("queryable") || !wmsLayer["queryable"]) {
				continue;
			}
			var queryUrl = null;
			if (wmsLayer.hasOwnProperty("queryUrl")) {
				queryUrl = wmsLayer["queryUrl"];
			} else {
				queryUrl = wmsLayer["baseUrl"];
			}

			var control = new OpenLayers.Control.WMSGetFeatureInfo({
				url : queryUrl,
				layerUrls : wmsLayer["baseUrl"],
				title : 'Identify features by clicking',
				queryVisible : true,
				infoFormat : 'application/vnd.ogc.gml',
				hover : false,
				drillDown : true,
				maxFeatures : 5,
				handlerOptions : {
					"click" : {
						'single' : true,
						'double' : false
					}
				},
				eventListeners : {
					getfeatureinfo : function(evt) {
						if (evt.features) {
							bus.send("info-features", [ evt.features, evt.xy.x, evt.xy.y ]);
						}
					},
					beforegetfeatureinfo : function() {
						control.layers = new Array();
						var id = wmsLayer["id"];
						var layer = map.getLayersByName(id)[0];
						control.layers.push(layer);

						if (layerIdTimestamp.hasOwnProperty(id)) {
							control.vendorParams = {
								"time" : layerIdTimestamp[id].toISO8601String()
							};
						}

						bus.send("clear-info-features");
					}
				},
				formatOptions : {
					typeName : 'XXX',
					featureNS : 'http://www.openplans.org/unredd'
				}
			});
			layerIdControl[wmsLayer["id"]] = control;
			controls.push(control);
		}
	});

	bus.listen("layer-timestamp-selected", function(e, id, timestamp) {
		layerIdTimestamp[id] = timestamp;
	});

	bus.listen("layers-loaded", function() {
		bus.send("set-default-exclusive-control", [ controls ]);
		bus.send("activate-default-exclusive-control");
	});
});
