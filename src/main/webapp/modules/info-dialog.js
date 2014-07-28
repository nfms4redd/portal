define([ "jquery", "message-bus", "map", "i18n", "customization" ], function($, bus, map, i18n, customization) {

	var wmsNamePortalLayerName = {};

	bus.listen("add-layer", function(event, layerInfo) {
		var portalLayerName = layerInfo["label"];
		$.each(layerInfo.wmsLayers, function(i, wmsLayer) {
			var wmsName = wmsLayer["wmsName"];
			wmsNamePortalLayerName[wmsName] = portalLayerName;
		});
	});

	bus.listen("info-features", function(event, features, x, y) {
		var i, infoPopup, dialogX, dialogY, epsg4326, epsg900913;

		// re-project to Google projection
		epsg4326 = new OpenLayers.Projection("EPSG:4326");
		epsg900913 = new OpenLayers.Projection("EPSG:900913");
		for (i = 0; i < features.length; i++) {
			if (customization["highlight-bounds"] == "true") {
				features[i].geometry = features[i].geometry.getBounds().toGeometry();
			}
			features[i].geometry.transform(epsg4326, epsg900913);
		}

		infoPopup = $("#info_popup");
		if (infoPopup.length === 0) {
			infoPopup = $("<div/>").attr("id", "info_popup");
		} else {
			infoPopup.empty();
		}
		infoPopup.dialog({
			title : i18n["info_dialog_title"],
			closeOnEscape : true,
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
				var tr = $("<tr/>").appendTo(tblData);

				var imgZoomToArea = $("<img/>").attr("src", "modules/images/zoom-to-object.png");
				imgZoomToArea.css("cursor", "pointer");
				$("<td/>").addClass("command").append(imgZoomToArea).appendTo(tr).click(function() {
					bus.send("zoom-to", feature.geometry.getBounds().scale(1.2));
				});

				var imgIndicators = $("<img/>").attr("src", "modules/images/object-indicators.png");
				imgIndicators.css("cursor", "pointer");
				imgIndicators.click(function() {
				});
				$("<td/>").addClass("command").append(imgIndicators).appendTo(tr);

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