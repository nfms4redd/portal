define([ "jquery", "message-bus", "layout", "map", "jquery-ui" ], function($, bus, layout, map) {
	var divActiveLayersContainer = layout.activeLayers;

	var divActiveLayers = $("<div/>").attr("id", "active_layers");

	// Accordion header
	var h3Title = $("<h3/>").html("Selected layers");
	divActiveLayers.append(h3Title);

	// div that contains all the active layers with sliders
	var div = $("<div/>");
	divActiveLayers.append(div);
	//div.empty();

	var table = $('<table style="width:100%;margin:auto"></table>');
	div.append(table);

	// create the accordion
	divActiveLayers.accordion({
		collapsible: false,
		autoHeight: false,
		animated: false,
		heightStyle: "content",
		create: function (event, ui) {
			$('#active_layers_pane .ui-icon-triangle-1-s').hide();
		}
	});

	divActiveLayersContainer.append(divActiveLayers);

	bus.listen("portal-layer-visibility", function(event, layerInfo) {
		var tr1, layerId, tdLegend, inlineLegend, visibility, colspan;

		visibility = layerInfo.active === true;

		layerId = layerInfo.id;
		colspan = 2;

		function addLayer(layerId) {
			// Layer label
			tr1 = $('<tr></tr>');

			tdLegend = null;
	    if (layerInfo.hasOwnProperty("inlineLegendUrl")) {
		    tdLegend = $("<td/>").addClass("layer_legend");
	      inlineLegend = $('<img class="inline-legend" src="' + layerInfo.inlineLegendUrl + '">');
	      tdLegend.append(inlineLegend);
	    }

	    if (tdLegend !== null) {
		    tr1.append(tdLegend);
		    colspan = 1
	    }

			tr1.append($('<td colspan="' + colspan + '">' + layerInfo.label + '</td>'));

			// Transparency slider
			var transparencyDiv = $('<div style="margin-top:4px; margin-bottom:12px;" id="' + 'layerId' + '_transparency_slider"></div>');
			var td = $('<td colspan="2"></td>');
			td.append(transparencyDiv);
			var tr2 = $('<tr id="' + layerId + '_tr2"></tr>');
			tr2.append(td);

			// Append elements to table
			table.append(tr1);
			table.append(tr2);

			$(transparencyDiv).slider({
				min: 0,
				max: 100,
				value: 100 * map.getLayer(layerId).opacity,
				slide: function (event, ui) {
					bus.send("transparency-slider-changed", [layerInfo, ui.value / 100]);
				}
			});
			divActiveLayers.accordion("refresh");
		}

		function delLayer(layerId) {
			$('#' + layerId + '_tr1').remove();
			$('#' + layerId + '_tr2').remove();
		}

		if (visibility) { addLayer(layerId); }
		else { delLayer(layerId); }
	});

	bus.listen("show-active-layer-list", function(event, groupInfo) {
//    divActiveLayers.accordion({
//      collapsible: false,
//      autoHeight: false,
//      animated: false,
//      clearStyle: true,
//      create: function (event, ui) {
//        $('#active_layers_pane .ui-icon-triangle-1-s').hide();
//        //updateActiveLayersPane(mapContexts);
//      }
//    });
		divActiveLayers.accordion("refresh");
		divActiveLayersContainer.show();
	});

	bus.listen("hide-active-layer-list", function(event, groupInfo) {
		divActiveLayersContainer.hide();
	});

	return divActiveLayers;
});
