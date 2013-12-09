define([ "jquery", "message-bus" ], function($, bus) {

	var renderers = new Array();

	bus.listen("add-layer-info-renderer", function(event, layerName, renderer) {
		renderers[layerName] = renderer;
	});

	bus.listen("info-features", function(event, features, x, y) {
		// re-project to Google projection
		var epsg4326 = new OpenLayers.Projection("EPSG:4326");
		var epsg900913 = new OpenLayers.Projection("EPSG:900913");
		for (var i = 0; i < features.length; i++) {
			features[i].geometry.transform(epsg4326, epsg900913);
		}

		var infoPopup = $("#info_popup");
		if (infoPopup.length == 0) {
			infoPopup = $("<div/>").attr("id", "info_popup");
		} else {
			infoPopup.empty();
		}
		infoPopup.dialog({
			closeOnEscape : true,
			width : 300,
			resizable : false,
			close : function(event, ui) {
				// destroy all features
				$.each(features, function(featureIndex, feature) {
					feature.destroy();
				});
			},
			autoOpen : false
		});

		$.each(features, function(layerId, feature) {
			var qualifiedLayerId = feature.gml.featureNSPrefix + ":" + feature.gml.featureType;
			var table = $("<table/>");
			var tr1 = $("<tr/>");
			var td1 = $('<td colspan="2" class="area_name" />');
			tr1.append(td1);
			table.append(tr1);
			table.mouseover(function() {
				bus.send("highlight-feature", [ feature ]);
			});
			table.mouseout(function() {
				bus.send("clear-highlighted-features");
			});
			var customRenderer = renderers[qualifiedLayerId];
			if (customRenderer) {
				td1.append(customRenderer(feature));
			} else {
				var info = $("<table/>");
				$.each(feature.attributes, function(index, attribute) {
					var tdIndex = $("<td/>").html(index);
					var tdAttribute = $("<td/>").html(":&nbsp;&nbsp;&nbsp;" + attribute);
					var trAttribute = $("<tr/>").append(tdIndex).append(tdAttribute);
					info.append(trAttribute);
				});
				td1.append(info);
			}

			tr2 = $("<tr/>");
			var td2 = $("<td class=\"td_left\" id=\"indicator_buttons_container_" + layerId + "\"/>");
			tr2.append(td2);
			table.append(tr2);

			td2.append("<img src=\"styles/images/ajax-loader.gif\" alt=\"wait\"/>");
			bus.send("ajax", {
				url : 'indicators?layerId=' + qualifiedLayerId,
				success : function(indicators, textStatus, jqXHR) {
					for (i = 0; i < indicators.length; i++) {
						id = "stats_link_" + layerId + "_" + indicators[i].id;
						$("#indicator_buttons_container_" + layerId).append(
								"<a style=\"color:white\" class=\"feature_link fancybox.iframe\" id=\"" + id + "\" href=\"indicator?objectId=" + feature.attributes[indicators[i].fieldId]
										+ "&layerId=" + qualifiedLayerId + "&indicatorId=" + indicators[i].id + "\">" + indicators[i].name + "</a>");
						$('#' + id).fancybox({
							maxWidth : 840,
							maxHeight : 600,
							fitToView : false,
							width : 840,
							height : 590,
							autoSize : false,
							closeClick : false,
							openEffect : 'none',
							closeEffect : 'fade'
						});
					}
				},
				errorMsg : "Could not obtain the indicator",
				complete : function() {
					$("#indicator_buttons_container_" + layerId).children("img").remove();
				}
			});

			// TODO: localize statistics and zoom to area buttons
			td3 = $("<td class=\"td_right\"/>");
			td3.append("<a style=\"color:white\" class=\"feature_link\" href=\"#\" id=\"zoom_to_feature_" + layerId + "\">Zoom to area</a>");
			tr2.append(td3);
			infoPopup.append(table);

			$("#zoom_to_feature_" + layerId).click(function() {
				bus.send("zoom-to", feature.geometry.getBounds().scale(1.2));
			});
		});

		// If no features selected then close the dialog
		if (features.length === 0) {
			console.log("closing the dialog");
			infoPopup.dialog('close');
		} else {
			// Don't reposition the dialog if already open
			if (!infoPopup.dialog('isOpen')) {
				var dialogX = x - 100;
				var dialogY = y - 200;
				infoPopup.dialog('option', 'position', [ dialogX, dialogY ]);

				// Finally open the dialog
				infoPopup.dialog('open');
				infoPopup.dialog('moveToTop');
			}
		}
	});
});