define([ "jquery", "message-bus", "map", "i18n" ], function($, bus, map, i18n) {

	bus.listen("info-features", function(event, features, x, y) {
		var i, infoPopup, dialogX, dialogY, epsg4326, epsg900913;

		// re-project to Google projection
		epsg4326 = new OpenLayers.Projection("EPSG:4326"),
		epsg900913 = new OpenLayers.Projection("EPSG:900913");
		for (i = 0; i < features.length; i++) {
			features[i].geometry.transform(epsg4326, epsg900913);
		}

		infoPopup = $("#info_popup");
		if (infoPopup.length === 0) {
			infoPopup = $("<div/>").attr("id", "info_popup");
		} else {
			infoPopup.empty();
		}
		infoPopup.dialog({
			closeOnEscape : true,
			width : 300,
			resizable : false,
			close : function(event, ui) {
				bus.send("clear-highlighted-features");
				map.getLayer("Highlighted Features").destroyFeatures();

				// destroy all features
//				$.each(features, function(featureIndex, feature) {
//					feature.destroy();
//				});
			},

			autoOpen : false
		});

		// TODO check if there is a custom pop up instead of showing the
		// standard one

		$.each(features, function(layerId, feature) {
			var qualifiedLayerId, table, tr1, td1, tr2, td2, td3, info;

			qualifiedLayerId = feature.gml.featureNSPrefix + ":" + feature.gml.featureType;

			table = $("<table/>");
			tr1 = $("<tr/>");
			td1 = $('<td colspan="2" class="area_name" />');
			tr1.append(td1);
			table.append(tr1);

			table.mouseenter(function() { bus.send("highlight-feature", feature); });
			table.mouseleave(function()  { bus.send("clear-highlighted-features"); });

			info = $("<table/>");
			$.each(feature.attributes, function(index, attribute) {
				var tdIndex = $("<td/>").html(index);
				var tdAttribute = $("<td/>").html(":&nbsp;&nbsp;&nbsp;" + attribute);
				var trAttribute = $("<tr/>").append(tdIndex).append(tdAttribute);

				info.append(trAttribute);
			});
			td1.append(info);

			tr2 = $("<tr/>");
			td2 = $("<td class=\"td_left\" id=\"indicator_buttons_container_" + layerId + "\"/>");
			tr2.append(td2);
			table.append(tr2);

			td2.append("<img src=\"styles/images/ajax-loader.gif\" alt=\"wait\"/>");

			bus.send("ajax", {
				url : 'indicators?layerId=' + qualifiedLayerId,
				success : function(indicators, textStatus, jqXHR) {
					var n, id;

					for (n = 0; n < indicators.length; n++) {
						id = "stats_link_" + layerId + "_" + indicators[n].id;
						$("#indicator_buttons_container_" + layerId).append(
								"<a style=\"color:white\" class=\"feature_link fancybox.iframe\" id=\"" + id + "\" href=\"indicator?objectId=" + feature.attributes[indicators[n].fieldId]
										+ "&layerId=" + qualifiedLayerId + "&indicatorId=" + indicators[n].id + "\">" + indicators[n].name + "</a>");
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
			td3.append("<a style=\"color:white\" class=\"feature_link\" href=\"#\" id=\"zoom_to_feature_" + layerId + "\">"+ i18n["zoom_to_area"] + "</a>");
			tr2.append(td3);
			infoPopup.append(table);

			$("#zoom_to_feature_" + layerId).click(function() {
				bus.send("zoom-to", feature.geometry.getBounds().scale(1.2));
			});
		});

		// If no features selected then close the dialog
		if (features.length === 0) {
			infoPopup.dialog('close');
		} else {
			// Don't reposition the dialog if already open
			if (!infoPopup.dialog('isOpen')) {
				dialogX = x + 100;
				dialogY = y - 200;
				infoPopup.dialog('option', 'position', [ dialogX, dialogY ]);

				// Finally open the dialog
				infoPopup.dialog('open');
				infoPopup.dialog('moveToTop');
			}
		}
	});

	bus.listen("highlight-feature", function(event, feature) {
		var highlightLayer = map.getLayer("Highlighted Features");
		highlightLayer.removeAllFeatures();
		highlightLayer.addFeatures(feature);
		highlightLayer.redraw();
	});

	bus.listen("clear-highlighted-features", function() {
		var highlightLayer = map.getLayer("Highlighted Features");
		highlightLayer.removeAllFeatures();
		highlightLayer.redraw();
	});

});