define([ "jquery", "message-bus", "layout", "customization", "i18n", "jquery-ui" ], function($, bus, layout, customization, i18n, ui) {
	var divLayerListSelector = layout.layerListSelector;

	var allLayersButton = $('<input type="radio" id="show_all_layers" name="layer_list_selector" checked="checked"><label for="show_all_layers">' + i18n.layers + '</label>');
	var activeLayersbutton = $('<input type="radio" id="show_active_layers" name="layer_list_selector"><label for="show_active_layers">' + i18n.selected_layers + '</label>');

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
