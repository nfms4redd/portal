define([ "module", "jquery", "message-bus", "map", "i18n", "customization", "highcharts", "highcharts-theme-sand" ], function(module, $, bus, map, i18n, customization) {

	var wmsNamePortalLayerName = {};

	var infoFeatures;

	bus.listen("add-layer", function(event, layerInfo) {
		var portalLayerName = layerInfo["label"];
		$.each(layerInfo.wmsLayers, function(i, wmsLayer) {
			var wmsName = wmsLayer["wmsName"];
			wmsNamePortalLayerName[wmsName] = portalLayerName;
		});
	});

	bus.listen("clear-info-features", function(event, features, x, y) {
		$("#info_popup").empty();
	});

	bus.listen("info-features", function(event, wmsLayerId, features, x, y) {
		var i, infoPopup, epsg4326, epsg900913;

		infoFeatures = features;

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

		var divResults = $("<div/>").attr("id", "result_area").appendTo(infoPopup);
		var layerNameFeatures = null;
		var layerName = wmsNamePortalLayerName[wmsLayerId];
		$("<div/>").addClass("layer_title info_center").html(layerName).appendTo(divResults);
		var divTable = $("<div/>").addClass("layer_results info_center").appendTo(divResults);
		var tblData = $("<table/>").appendTo(divTable);
		var tr = $("<tr/>").appendTo(tblData);

		$("<th/>").addClass("command").html("").appendTo(tr);
		$("<th/>").addClass("command").html("").appendTo(tr);
		var aliases = features[0]["aliases"];
		for (var i = 0; i < aliases.length; i++) {
			$("<th/>").addClass("data").html(aliases[i].alias + "(" + aliases[i].name + ")").appendTo(tr);
		}
		$.each(features, function(index, feature) {

			var tr = $("<tr/>").appendTo(tblData);

			// Zoom to object button
			var imgZoomToArea = $("<img/>").attr("id", "info-magnifier-" + feature["index"]).attr("src", "modules/images/zoom-to-object.png");
			imgZoomToArea.css("cursor", "pointer");
			var tdMagnifier = $("<td/>").addClass("command").appendTo(tr);
			var bounds = null;
			var highlightGeom = null;

			if (feature.geometry) {
				bounds = feature["geometry"].getBounds();
				highlightGeom = feature["geometry"];
			} else if (feature.attributes["bbox"]) {
				var bbox = feature.attributes["bbox"];
				bounds = new OpenLayers.Bounds();
				bounds.extend(new OpenLayers.LonLat(bbox[0], bbox[1]));
				bounds.extend(new OpenLayers.LonLat(bbox[2], bbox[3]));
				highlightGeom = bounds.toGeometry();
			}
			if (bounds != null) {
				tdMagnifier.append(imgZoomToArea);
				tdMagnifier.click(function() {
					bus.send("zoom-to", bounds.scale(1.2));
				});
			}

			// Indicators button
			var imgWait = $("<img/>").attr("src", "styles/images/ajax-loader.gif").attr("alt", "wait");
			var tdIndicators = $("<td/>").attr("id", "info-indicator-" + feature["index"]).addClass("command").append(imgWait.clone()).appendTo(tr);
			bus.send("ajax", {
				url : 'indicators?layerId=' + wmsLayerId,
				success : function(indicators, textStatus, jqXHR) {
					if (indicators.length > 0) {
						bus.send("feature-indicators-received", [ feature["index"], indicators ]);
					}
				},
				errorMsg : "Could not obtain the indicator",
				complete : function() {
					imgWait.remove();
				}
			});

			var aliases = feature["aliases"];
			for (var i = 0; i < aliases.length; i++) {
				$("<td/>").addClass("data").html(feature.attributes[aliases[i].name]).appendTo(tr);
			}

			if (highlightGeom != null) {
				tr.mouseenter(function() {
					bus.send("highlight-feature", highlightGeom);
				});
				tr.mouseleave(function() {
					bus.send("clear-highlighted-features");
				});
			}

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
				bus.send("show-feature-indicator", [ infoFeatureIndex, indicatorIndex ]);
			});
			// TODO Agregar separador entre iconos.
		});// END each
	})

	bus.listen("show-feature-indicator", function(event, featureIndex, indicatorIndex) {
		var feature = infoFeatures[featureIndex];
		var indicator = feature["indicators"][indicatorIndex];
		var layerId = feature["layerId"];

		bus.send("ajax", {
			url : "indicator?objectId=" + feature.attributes[indicator.idField] + //
			"&objectName=" + feature.attributes[indicator.nameField] + //
			"&layerId=" + layerId + //
			"&indicatorId=" + indicator.id,
			success : function(chartData, textStatus, jqXHR) {
				var chart = $("<div/>");
				chart.highcharts(chartData);
				bus.send("show-info", [ indicator.title, chart ]);
			},
			errorMsg : "Could not obtain the indicator"
		});

	});

	bus.listen("highlight-feature", function(event, geometry) {
		var highlightLayer = map.getLayer("Highlighted Features");
		highlightLayer.removeAllFeatures();
		var feature = new OpenLayers.Feature.Vector();
		feature.geometry = geometry;
		highlightLayer.addFeatures(feature);
		highlightLayer.redraw();
	});

	bus.listen("clear-highlighted-features", function() {
		var highlightLayer = map.getLayer("Highlighted Features");
		highlightLayer.removeAllFeatures();
		highlightLayer.redraw();
	});

});