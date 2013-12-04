define([ "jquery", "message-bus", "layout", "customization", "i18n", "jquery-ui", "layer-list" ], function($, bus, layout, customization, i18n, ui, layerList) {
	var divLayerListSelector = layout.layerListSelector;

	var radio1 = $('<input type="radio" id="all_layers" name="layer_list_selector" checked="checked"></input><label for="all_layers">' + i18n.layers + '</label>');
	var radio2 = $('<input type="radio" id="active_layers" name="layer_list_selector"></input><label for="active_layers">' + i18n.selected_layers + '</label>');

	divLayerListSelector.append(radio1);
	divLayerListSelector.append(radio2);

	radio1.click(function() {
		bus.send("show-layer-list");
		bus.send("hide-active-layer-list");
	});

	radio2.click(function () {
		bus.send("show-active-layer-list");
		bus.send("hide-layer-list");
		/*
		 $("#layers_pane").hide();
		 $("#active_layers_pane").accordion({
		 collapsible: false,
		 autoHeight: false,
		 animated: false,
		 create: function (event, ui) {
		 $('#active_layers_pane .ui-icon-triangle-1-s').hide();
		 updateActiveLayersPane(mapContexts);
		 }
		 });
		 $("#active_layers_pane").show();
		 */
	});

	divLayerListSelector.buttonset();
	divLayerListSelector.show();

	return divLayerListSelector;
});
