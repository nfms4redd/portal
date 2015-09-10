/**
 * @author Micho García
 */

'use strict';

define(["module", "toolbar", "i18n", "jquery", "message-bus", "map"], function(module, toolbar, i18n, $, bus, map) {
	
	var dialog = null;
	var divContent = null;

	/**
	 * Esta función está también en legend-panel.js. Si se repite en varios sitios puede convenir refactorizar y crear un módulo que
	 * cree elementos de la UI. De momento no abrimos ese pastel.
	 */
	var getDialog = function() {
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
				height : 300,
				minHeight : 400,
				maxHeight : 400,
				width : 325,
				zIndex : 2000,
				resizable : true
			});
		}

		return dialog;
	};

	var btn = $("<a/>").attr("id", "order-button").addClass("blue_button").html(i18n["layer_order"]);
	btn.appendTo(toolbar);
	btn.click(function() {
		var dialog = getDialog();
		if (!dialog.dialog("isOpen")) {
			getDialog().dialog("open");
		} else {
			getDialog().dialog("close");
		}
	});

	var layers = [];

	var addLayer_ = function(evt, layer) {
		for (var n in layer.wmsLayers) {
			layers.push(layer.wmsLayers[n]);
		}
	}

	var loadLayers_ = function(evt) {
		var sorted = layers.sort(function(a, b) {
			return a.zIndex - b.zIndex
		});

		for (var i = 0; i < sorted.length; i++) {
			insertLayerOnControl(sorted[i]);
		}
	}

	var insertLayerOnControl = function(layer) {

	}

	bus.listen('add-layer', addLayer_);
	bus.listen('layers-loaded', loadLayers_);
});
