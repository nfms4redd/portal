/**
 * @author Micho García
 */

'use strict';

define(["module", "toolbar", "i18n", "jquery", "message-bus", "map", "jquery-ui"], function(module, toolbar, i18n, $, bus, map) {
	
	var dialog = null;
	var divContent = null;
	var layers = [];
	var layersLoaded = false;

	/**
	 * This function is also in legend-panel.js. Maybe is a good idea refactor it and extract a class to
	 * generate UI components. But today is not the moment to do this. Micho García <micho.garcia@geomati.co>
	 */
	var getDialog_ = function() {
		if (dialog == null) {
			dialog = $("<div/>");
			dialog.attr("title", i18n["layer_order"]);
			dialog.attr("id", "layer_order_pane");
			divContent = $("<div/>");
			divContent.appendTo(dialog);
			divContent.attr("id", "layer_order_content");
			dialog.dialog({
				position : {
					my : "left top",
					at : "right bottom+15",
					of : "#order-button"
				},
				closeOnEscape : false,
				autoOpen : false,
				height : 500,
				minHeight : 400,
				width : 325,
				zIndex : 2000,
				resizable : true,
				close: onCloseDialog_
			});
			divContent.sortable({
				cursor: "move"
			});
			divContent.on('sortstop', onChangeLayerPosition_);
		}

		return dialog;
	};
	
	var onChangeLayerPosition_ = function(evt, ui) {
		var newLayersOrder = divContent.sortable('toArray');
		for (var i = 0; i < newLayersOrder.length; i++) {
			var id = newLayersOrder[i];
			var layer = map.getLayer(id);
			if (layer) {
				map.setLayerIndex(layer, i);
				// TODO: propagate change to other modules (and persist it in layers.json).
			}
		}
	}
	
	var onCloseDialog_ = function(evt) {
		divContent.empty();
	}

	var btn = $("<a/>").attr("id", "order-button").addClass("blue_button").html(i18n["layer_order"]);
	btn.appendTo(toolbar);
	btn.click(function() {
		layers = map.layers;
		var dialog = getDialog_();
		if (!dialog.dialog("isOpen")) {
			getDialog_().dialog("open");
			showLayersOnDialog_();
		} else {
			getDialog_().dialog("close");
		}
	});
	
	var insertLayerOnControl_ = function(layer) {
		var item = $('<div>').attr('id', layer.id).addClass('layer-order-item');
		var label = $('<span>').addClass('layer-name-item').html(layer.name);
		item.append(label).appendTo(divContent);
	}
	
	var showLayersOnDialog_ = function() {
		if (layersLoaded) {
			for (var n in layers) {
				var layer = layers[n];
				insertLayerOnControl_(layer);
			}
		} else {
			// TODO poner aquí un gif loading
		}
	}

	var loadLayers_ = function() {
		layersLoaded = true;
	}

	bus.listen("reset-layers", function() {
		layers = [];
		layersLoaded = false;
	});

	bus.listen('layers-loaded', loadLayers_);
});
