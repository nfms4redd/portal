define([ "olmap" ], function(map) {
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
					showInfo(evt);
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

	$(document).bind("add-layer", function(event, layerData) {
		if (layerData.queriable) {
			control.layers.push(layerData.wmsName);
			if (control.url == null) {
				control.url = url;
			}
		}
	});
});

// var showInfo = function(evt) {
// var x = evt.xy.x - 100, y = evt.xy.y - 200, i, feature, featureType,
// nSelectedFeatures = 0, infoPopup = $("#info_popup");
//
// highlightLayer.destroyFeatures();
// selectedFeatures = {};
//
// if (evt.features && evt.features.length) {
// var viewportExtent = UNREDD.map.getExtent();
//
// // re-project to Google projection
// for (i = 0; i < evt.features.length; i++) {
// evt.features[i].geometry.transform(new OpenLayers.Projection("EPSG:4326"),
// new OpenLayers.Projection("EPSG:900913"));
//
// // don't select it if most of the polygon falls outside of the
// // viewport
// if
// (!viewportExtent.scale(1.3).containsBounds(evt.features[i].geometry.getBounds()))
// {
// continue;
// }
//
// feature = evt.features[i];
// featureType = feature.gml.featureType;
// selectedFeatures[featureType] = feature;
// nSelectedFeatures += 1;
// }
//
// infoPopup.empty();
//
// // handle custom popup - info will be taken from json but for now
// // it's in the custom.js. Don't have time
// var customPopupLayer = null;
// $.each(selectedFeatures, function(layerId, feature) {
// if (UNREDD.layerInfo.hasOwnProperty(layerId)) {
// info = UNREDD.layerInfo[layerId](feature);
// if (typeof (info.customPopup) !== "undefined") {
// customPopupLayer = layerId;
// info.customPopup();
//
// $.fancybox({
// href : '#custom_popup'
// });
//
// return false; // only show the custom info dialog for
// // the first layer that has it
// }
// }
//
// return true;
// });
//
// if (customPopupLayer !== null) {
// // infoPopup.dialog('close');
// return;
// }
//
// $.each(selectedFeatures, function(layerId, feature) {
// var qualifiedLayerId = feature.gml.featureNSPrefix + ":" +
// feature.gml.featureType;
// var table, info, td1, td2, td3, tr1, tr2, tr3;
//
// if (UNREDD.layerInfo.hasOwnProperty(layerId)) {
// info = UNREDD.layerInfo[layerId](feature);
// } else {
// info = genericInfoContent(feature);
// }
//
// table = $("<table>");
// tr1 = $("<tr/>");
// td1 = $('<td colspan="2" class="area_name" />');
// tr1.append(td1);
// table.append(tr1);
// table.mouseover(function() {
// highlightLayer.removeAllFeatures();
// highlightLayer.addFeatures(feature);
// highlightLayer.redraw();
// });
// table.mouseout(function() {
// highlightLayer.removeAllFeatures();
// highlightLayer.redraw();
// });
// td1.append(info.title().toLowerCase());
//
// tr2 = $("<tr/>");
// td2 = $("<td class=\"td_left\"/>");
// tr2.append(td2);
// table.append(tr2);
//
// td2.append("<img src=\"images/ajax-loader.gif\" alt=\"wait\"/>");
// $.ajax({
// url : 'indicators.json?layerId=' + qualifiedLayerId,
// success : function(indicators, textStatus, jqXHR) {
// td2.empty();
// for (i = 0; i < indicators.length; i++) {
// id = "stats_link_" + layerId + "_" + indicators[i].id;
// td2.append("<a style=\"color:white\" class=\"feature_link fancybox.iframe\"
// id=\"" + id
// + "\" href=\"indicator.json?objectId=" +
// feature.attributes[indicators[i].fieldId] + "&layerId="
// + qualifiedLayerId + "&indicatorId=" + indicators[i].id + "\">" +
// indicators[i].name + "</a>");
// $('#' + id).fancybox({
// maxWidth : 840,
// maxHeight : 600,
// fitToView : false,
// width : 840,
// height : 590,
// autoSize : false,
// closeClick : false,
// openEffect : 'none',
// closeEffect : 'fade'
// });
// }
// },
// error : function(jqXHR, textStatus, errorThrown) {
// td2.empty();
// var errorMessage = $.parseJSON(jqXHR.responseText).message;
// alert(messages[errorMessage]);
// }
// });
//
// // TODO: localize statistics and zoom to area buttons
// td3 = $("<td class=\"td_right\"/>");
// td3.append("<a style=\"color:white\" class=\"feature_link\" href=\"#\"
// id=\"zoom_to_feature_" + layerId + "\">Zoom to area</a>");
// tr2.append(td3);
// infoPopup.append(table);
//
// if (info.info && info.info()) {
// tr3 = $("<tr/>");
// td3 = $("<td class=\"td_left\" colspan=\"2\"/>");
// tr3.append(td3);
// table.append(tr3);
// td3.append(info.info());
// }
//
// $('#drivers_data_link').fancybox({
// 'autoScale' : false,
// 'type' : 'iframe',
// 'scrolling' : 'no',
// 'width' : 500,
// 'height' : 600,
// 'openEffect' : 'none',
// 'closeEffect' : 'fade'
// });
//
// $("#zoom_to_feature_" + layerId).click(function() {
// UNREDD.map.zoomToExtent(feature.geometry.getBounds().scale(1.2));
// });
// });
// }
//
// var totalHeight = 0;
//
// // If no features selected then close the dialog
// if (nSelectedFeatures === 0) {
// infoPopup.dialog('close');
// } else {
// // Don't reposition the dialog if already open
// if (!infoPopup.dialog('isOpen')) {
// infoPopup.dialog('option', 'position', [ x, y ]);
//
// // Finally open the dialog
// infoPopup.dialog('open');
// }
// }
// };
