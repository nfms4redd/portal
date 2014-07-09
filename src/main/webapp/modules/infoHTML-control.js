define([ "map", "message-bus", "customization" ], function(map, bus, customization) {

	var ouptputGetFeatureInfoGML;
	var controlFeatures = new OpenLayers.Control.WMSGetFeatureInfo({
		url : customization.queryURL,
		layerUrls : [ customization.layerURL ],
		title : 'get polygon as GML by clicking',
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
				if (evt.features && evt.features.length) {
					ouptputGetFeatureInfoGML = evt.features;
					// re-project to Google projection
					epsg4326 = new OpenLayers.Projection("EPSG:4326"),
					epsg900913 = new OpenLayers.Projection("EPSG:900913");
					for (i = 0; i < ouptputGetFeatureInfoGML.length; i++) {
						ouptputGetFeatureInfoGML[i].geometry.transform(epsg4326, epsg900913);
					}
					controlText.activate();
					controlText.request(evt.xy);
					controlText.deactivate();
				}
			}
		},
		formatOptions : {
			typeName : 'XXX',
			featureNS : 'http://www.openplans.org/unredd'
		}
	});
	
	var controlText = new OpenLayers.Control.WMSGetFeatureInfo({
		url : customization.queryURL,
		layerUrls : [ customization.layerURL ],
		title : 'get featureInfo by clicking',
		queryVisible : true,
		infoFormat : 'text/html',
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
					bus.send("infoHTML-text", [ "html", evt.text, evt.xy.x, evt.xy.y, ouptputGetFeatureInfoGML ]);
				}
			}
		}
	});

	bus.send("set-default-exclusive-control", [controlFeatures, controlText]);
	bus.send("activate-default-exclusive-control");
	
	bus.send("activateGetFeatureInfoControls", [controlFeatures, controlText]);

	bus.listen("maplayer-added", function(event, layer, layerInfo) {
		if (layerInfo.queryable === true) {
			if (controlFeatures.layers == null) {
				controlFeatures.layers = new Array();
			}
			if (controlText.layers == null) {
				controlText.layers = new Array();
			}
			controlFeatures.layers.push(layer);
			controlText.layers.push(layer);
		}
	});
});
