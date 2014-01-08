define([ "jquery", "message-bus", "layout", "customization", "i18n", "jquery-ui", "layer-list" ], function($, bus, layout, customization, i18n, ui, layerList) {
	var divLayerListSelector = layout.layerListSelector;

	var allLayersButton = $('<input type="radio" id="all_layers" name="layer_list_selector" checked="checked"><label for="all_layers">' + i18n.layers + '</label>');
	var activeLayersbutton = $('<input type="radio" id="active_layers" name="layer_list_selector"><label for="active_layers">' + i18n.selected_layers + '</label>');

	divLayerListSelector.append(allLayersButton);
	divLayerListSelector.append(activeLayersbutton);

    allLayersButton.click(function() {
		bus.send("show-layer-list");
		bus.send("hide-active-layer-list");
	});

    activeLayersbutton.click(function () {
		bus.send("show-active-layer-list");
		bus.send("hide-layer-list");
	});

	divLayerListSelector.buttonset();
	divLayerListSelector.show();

	return divLayerListSelector;
});
