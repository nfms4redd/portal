define([ "map", "message-bus", "customization" ], function(map, bus, customization) {

	var layerIds = new Array();
	var lastTimestamp = null;

	var control = new OpenLayers.Control.WMSGetFeatureInfo({
		url : customization["info.queryUrl"],
		layerUrls : [ customization["info.layerUrl"] ],
		title : 'Identify features by clicking',
		queryVisible : true,
		infoFormat : 'application/vnd.ogc.gml',
		hover : false,
		drillDown : false,
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
				for (var i = 0; i < layerIds.length; i++) {
					var layer = map.getLayersByName(layerIds[i])[0];
					control.layers.push(layer);
				}
				
				if (lastTimestamp != null) {
					control.vendorParams = {
						"time" : lastTimestamp.toISO8601String()
					};
				}
			}
		},
		formatOptions : {
			typeName : 'XXX',
			featureNS : 'http://www.openplans.org/unredd'
		}
	});

	bus.send("set-default-exclusive-control", [ control ]);
	bus.send("activate-default-exclusive-control");

	bus.listen("add-layer", function(event, layerInfo) {
		$.each(layerInfo.wmsLayers, function(index, wmsLayer) {
			if (wmsLayer.queryable) {
				layerIds.push(wmsLayer.id);
			}
		});
	});
	bus.listen("time-slider.selection", function(event, timestamp) {
		lastTimestamp = timestamp;
	});
});
