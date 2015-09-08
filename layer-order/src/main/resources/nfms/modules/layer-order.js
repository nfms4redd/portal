/**
 * @author Micho García
 */

'use strict';

define(["module", "layout", "i18n", "jquery", "message-bus", "map"], function(module, layout, i18n, $, bus, map) {

	var layers = [];

	var addLayer = function(evt, layer) {
		for (var n in layer.wmsLayers) {
			layers.push(layer.wmsLayers[n]);
		}
	}
	
	var loadLayers = function(evt) {
		var sorted = layers.sort(function(a, b) {
			return a.zIndex - b.zIndex
		});

		for (var i = 0; i < sorted.length; i++) {
			console.log(layers[i].zIndex);
		}
	}
	
	bus.listen('add-layer', addLayer);
	bus.listen('layers-loaded', loadLayers);

});
