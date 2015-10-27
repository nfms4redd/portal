// layer-list is imported to have it first in the list
define([ "jquery", "message-bus", "i18n", "jquery-ui", "layer-list" ], function($, bus, i18n) {

	/*
	 * keep the information about the layers that will be necessary when they
	 * become visible
	 */
	var layersInfo = {};

	// Create the div
	var divTransparency = $("<div/>").attr("id", "layers_transparency").addClass("layer_container_panel");

	var table = $('<table style="width:100%;margin:auto"></table>');
	divTransparency.append(table);

	bus.send("register-layer-panel", {
		id : "layers_transparency_selector",
		priority : 20,
		text : i18n.selected_layers,
		div : divTransparency
	});

	bus.listen("add-layer", function(event, layerInfo) {
		// set the visibility flag to true if the layer is active and if it is
		// not a placeholder (placeholder means that no geospatial data to show
		// are associated)
		if (!layerInfo.isPlaceholder) {
			var activeLayerInfo = {
				label : layerInfo.label,
				opacity : 1
			};
			if (layerInfo.hasOwnProperty("inlineLegendUrl")) {
				activeLayerInfo["inlineLegendUrl"] = layerInfo.inlineLegendUrl;
			}
			layersInfo[layerInfo.id] = activeLayerInfo;
		}
	});

	bus.listen("layer-visibility", function(event, layerId, visibility) {
		var tr1, tdLegend, inlineLegend, colspan;

		var layerInfo = layersInfo[layerId];
		if (layerInfo) {

			colspan = 2;

			function addLayer(layerId) {
				// Layer label
				tr1 = $('<tr id="' + layerId + '_tr1"></tr>');

				tdLegend = null;
				if (layerInfo.hasOwnProperty("inlineLegendUrl")) {
					tdLegend = $("<td/>").addClass("layer_legend");
					inlineLegend = $('<img class="inline-legend" src="' + layerInfo.inlineLegendUrl + '">');
					tdLegend.append(inlineLegend);
				}

				if (tdLegend !== null) {
					tr1.append(tdLegend);
					colspan = 1;
				}

				tr1.append($('<td colspan="' + colspan + '">' + layerInfo.label + '</td>'));

				// Transparency slider
				var transparencyDiv = $('<div style="margin-top:4px; margin-bottom:12px;" id="' + layerId + '_transparency_slider"></div>');
				var td = $('<td colspan="2"></td>');
				td.append(transparencyDiv);
				var tr2 = $('<tr id="' + layerId + '_tr2"></tr>');
				tr2.append(td);

				// Append elements to table
				table.append(tr1);
				table.append(tr2);

				transparencyDiv.slider({
					min : 0,
					max : 100,
					value : 100 * layerInfo.opacity,
					slide : function(event, ui) {
						bus.send("transparency-slider-changed", [ layerId, ui.value / 100 ]);
					}
				});
			}

			function delLayer(layerId) {
				$('#' + layerId + '_tr1').remove();
				$('#' + layerId + '_tr2').remove();
			}

			if (visibility) {
				addLayer(layerId);
			} else {
				delLayer(layerId);
			}
		}
	});

	bus.listen("transparency-slider-changed", function(event, layerId, opacity) {
		var layerInfo = layersInfo[layerId];
		if (layerInfo) {
			layerInfo["opacity"] = opacity;
		}

		var slider = $("#" + layerId + "_transparency_slider");
		var opacityPercentage = 100 * opacity;
		if (slider.slider("value") != opacityPercentage) {
			slider.slider("value", opacityPercentage);
		}
	});

	return divTransparency;
});
