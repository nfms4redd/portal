define([ "jquery", "olmap" ], function($, map) {

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
					$(document).trigger("info-features", [ evt.features, evt.xy.x, evt.xy.y ]);
				}
			}
		},
		formatOptions : {
			typeName : 'XXX',
			featureNS : 'http://www.openplans.org/unredd'
		}
	});

	map.addControl(control);
	control.activate();

	$(document).bind("maplayer-added", function(event, layer, layerInfo) {
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
