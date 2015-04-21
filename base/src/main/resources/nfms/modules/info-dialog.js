define([ "module", "jquery", "message-bus", "map", "i18n", "customization" ], function(module, $, bus, map, i18n, customization) {

	var wmsNamePortalLayerName = {};

	var infoFeatures;
	
	bus.listen("add-layer", function(event, layerInfo) {
		var portalLayerName = layerInfo["label"];
		$.each(layerInfo.wmsLayers, function(i, wmsLayer) {
			var wmsName = wmsLayer["wmsName"];
			wmsNamePortalLayerName[wmsName] = portalLayerName;
		});
	});

	bus.listen("info-features", function(event, features, x, y) {
		var i, infoPopup, epsg4326, epsg900913;

		infoFeatures = features;

		// re-project to Google projection
		epsg4326 = new OpenLayers.Projection("EPSG:4326");
		epsg900913 = new OpenLayers.Projection("EPSG:900913");
		for (i = 0; i < features.length; i++) {
			/*
			 * Raster layers in GeoServer return no Geometry
			 */
			if (features[i].geometry) {
				if (customization["highlight-bounds"] == "true") {
					features[i].geometry = features[i].geometry.getBounds().toGeometry();
				}
				features[i].geometry.transform(epsg4326, epsg900913);
			}
		}

		infoPopup = $("#info_popup");
		if (infoPopup.length === 0) {
			infoPopup = $("<div/>").attr("id", "info_popup");
		} else {
			infoPopup.empty();
		}
		infoPopup.dialog({
			title : i18n["info_dialog_title"],
			closeOnEscape : false,
			width : 700,
			height : 200,
			resizable : true,
			close : function(event, ui) {
				bus.send("clear-highlighted-features");
				map.getLayer("Highlighted Features").destroyFeatures();
			},
			autoOpen : false
		});

		// TODO check if there is a custom pop up instead of showing the
		// standard one

		var layerNameFeatures = {};
		$.each(features, function(layerId, feature) {
			qualifiedLayerId = feature.gml.featureNSPrefix + ":" + feature.gml.featureType;

			if (!layerNameFeatures.hasOwnProperty(qualifiedLayerId)) {
				layerNameFeatures[qualifiedLayerId] = [ feature ];
			} else {
				layerNameFeatures[qualifiedLayerId].push(feature);
			}
		});
		var divResults = $("<div/>").attr("id", "result_area").appendTo(infoPopup);
		$.each(layerNameFeatures, function(layerId, layerFeatures) {
			var layerName = wmsNamePortalLayerName[layerId];
			$("<div/>").addClass("layer_title info_center").html(layerName).appendTo(divResults);
			var divTable = $("<div/>").addClass("layer_results info_center").appendTo(divResults);
			var tblData = $("<table/>").appendTo(divTable);
			var tr = $("<tr/>").appendTo(tblData);

			$("<th/>").addClass("command").html("").appendTo(tr);
			$("<th/>").addClass("command").html("").appendTo(tr);
			for (attribute in layerFeatures[0].attributes) {
				$("<th/>").addClass("data").html(attribute).appendTo(tr);
			}
			$.each(layerFeatures, function(index, feature) {
				feature["layerId"] = layerId;
				
				var tr = $("<tr/>").appendTo(tblData);

				// Zoom to object button
				var imgZoomToArea = $("<img/>").attr("id", "info-magnifier-" + index).attr("src", "modules/images/zoom-to-object.png");
				imgZoomToArea.css("cursor", "pointer");
				var tdMagnifier = $("<td/>").addClass("command").appendTo(tr);
				if (feature.geometry) {
					tdMagnifier.append(imgZoomToArea);
					tdMagnifier.click(function() {
						bus.send("zoom-to", feature.geometry.getBounds().scale(1.2));
					});
				}

				// Indicators button
				var imgWait = $("<img/>").attr("src", "styles/images/ajax-loader.gif").attr("alt", "wait");
				var tdIndicators = $("<td/>").attr("id", "info-indicator-" + index).addClass("command").append(imgWait).appendTo(tr);
				bus.send("ajax", {
					url : 'indicators?layerId=' + layerId,
					success : function(indicators, textStatus, jqXHR) {
						if (indicators.length > 0) {
							bus.send("feature-indicators-received", [ index, indicators ]);
						}
					},
					errorMsg : "Could not obtain the indicator",
					complete : function() {
						imgWait.remove();
					}
				});

				var attributes = feature.attributes;
				for (attribute in attributes) {
					$("<td/>").addClass("data").html(attributes[attribute]).appendTo(tr);
				}

				tr.mouseenter(function() {
					bus.send("highlight-feature", feature);
				});
				tr.mouseleave(function() {
					bus.send("clear-highlighted-features");
				});

			});

		});

		// If no features selected then close the dialog
		if (features.length === 0) {
			infoPopup.dialog('close');
		} else {
			var openInCenter = module.config()["open-in-center"];
			// Don't reposition the dialog if already open
			if (!infoPopup.dialog('isOpen')) {
				var position;
				if (openInCenter) {
					position = {
						my : "center",
						at : "center",
						of : window
					};
				} else {
					var dialogX = x;
					var dialogY = y;
					position = {
						my : "left top",
						at : "left+" + dialogX + " top+" + dialogY,
						of : window,
						collision : "fit"
					};
				}

				infoPopup.dialog('option', 'position', position);

				// Finally open the dialog
				infoPopup.dialog('open');
				infoPopup.dialog('moveToTop');
			}
		}
	});

	bus.listen("feature-indicators-received", function(event, infoFeatureIndex, indicators) {
		infoFeatures[infoFeatureIndex]["indicators"] = indicators;
		// TODO if there is more than one indicator, offer the
		// choice to the user.
		$(indicators).each(function(indicatorIndex, indicator) {
			// Muestra un icono para cada grafico con el
			// texto alternativo con el titulo del grafico.
			var aIndicators = $("<a/>").attr("id", "info-indicator-" + infoFeatureIndex + "-" + indicatorIndex).addClass("fancybox.iframe").appendTo($("#info-indicator-" + infoFeatureIndex));
			aIndicators.css("padding", "1px");
			$("<img/>").attr("src", "modules/images/object-indicators.png").appendTo(aIndicators);
			aIndicators.attr("alt", indicator.title);
			aIndicators.attr("title", indicator.title);
			aIndicators.click(function() {
				bus.send("show-feature-indicator", [ infoFeatureIndex, indicatorIndex]);
			});
			// TODO Agregar separador entre iconos.
		});// END each
	})

	bus.listen("show-feature-indicator", function(event, featureIndex, indicatorIndex){
		var feature = infoFeatures[featureIndex];
		var indicator = feature["indicators"][indicatorIndex];
		var layerId = feature["layerId"];
		bus.send("show-info", [ indicator.title, "indicator?objectId=" + feature.attributes[indicator.fieldId] + "&layerId=" + layerId + "&indicatorId=" + indicator.id, {
			maxWidth : 800,
			maxHeight : 520,
			fitToView : false,
			width : 800,
			height : 520,
			autoSize : false,
			closeClick : false,
			openEffect : 'none',
			closeEffect : 'fade'
		} ]);
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