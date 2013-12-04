define([ "jquery", "message-bus", "layout", "jquery-ui" ], function($, bus, layout) {
	var divActiveLayers = layout.activeLayers;

    divActiveLayers.empty();
    //bus.listen("layer-visibility", [ layerInfo.id, checked ]);
    bus.listen("layer-visibility", function(event, layerId, visibility) {
        //console.log(layerId);
        //console.log(visibility);
    });







    if (false) {
        updateActiveLayersPane = function () {
            var //div,
                table, tr, td, td2, layers, inlineLegend, transparencyDiv;
            // empty the active_layers div (layer on the UI -> context here)
            $('#active_layers_pane div').empty();

            table = $('<table style="width:90%;margin:auto"></table>');
            $('#active_layers_pane div').append(table);

            jQuery.each(UNREDD.mapContexts, function (contextName, context) {
                var contextConf = context.configuration;

                if (contextConf.active) {
                    // First row: inline legend and context name
                    tr = $('<tr></tr>');

                    if (contextConf.hasOwnProperty('inlineLegendUrl')) {
                        td = $('<td style="width:20px"></td>');
                        inlineLegend = $('<img class="inline-legend" src="' + UNREDD.wmsServers[0] + contextConf.inlineLegendUrl + '">');
                        td.append(inlineLegend);
                        tr.append(td);
                        td2 = $('<td></td>');
                    } else {
                        td2 = $('<td colspan="2"></td>');
                    }
                    td2.append(contextConf.label);
                    tr.append(td2);
                    table.append(tr);

                    // Another row
                    tr = $('<tr></tr>');
                    transparencyDiv = $('<div style="margin-top:4px; margin-bottom:12px;" id="' + contextName + '_transparency_slider"></div>');
                    td = $('<td colspan="2"></td>');
                    td.append(transparencyDiv);
                    tr.append(td);
                    table.append(tr);

                    layers = contextConf.layers;

                    (function (contextLayers) {
                        $(transparencyDiv).slider({
                            min: 0,
                            max: 100,
                            value: 100,
                            slide: function (event, ui) {
                                $.each(contextLayers, function (n, layer) {
                                    layer.olLayer.setOpacity(ui.value / 100);
                                });
                            }
                        });
                    }(context.layers));
                }
            });
        };
    }

	return divActiveLayers;
});
