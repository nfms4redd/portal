define([ "olmap", "message-bus" ], function(map, bus) {

	var control = new OpenLayers.Control.WMSGetFeatureInfo({
		url : null,
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
				if (evt.features && evt.features.length) {
					bus.send("info-features", [ evt.features, evt.xy.x, evt.xy.y ]);
				}
			}
		},
		formatOptions : {
			typeName : 'XXX',
			featureNS : 'http://www.openplans.org/unredd'
		}
	});

	bus.send("set-default-exclusive-control", [control]);
	bus.send("activate-default-exclusive-control");

	bus.listen("maplayer-added", function(event, layer, layerInfo) {
		if (layerInfo.queryable) {
			if (control.layers == null) {
				control.layers = new Array();
			}
			control.layers.push(layer);
			if (control.url == null) {
				control.url = layerInfo.url;
			}
		}
	});
});
