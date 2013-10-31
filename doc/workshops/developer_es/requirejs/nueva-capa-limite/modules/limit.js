define([ "jquery", "map" ], function($, map) {

	return {
		addLayer : function(layer) {
			var layers = map.layers;
			for ( var i = 0; i < layers.length; i++) {
				/*
				 * Podemos hacer esto porque sabemos que son capas WMS
				 */
				if (layers[i].url == layer.url && layers[i].params["LAYERS"] == layer.params["LAYERS"]) {
					window.alert("¡¡Capa duplicada!!");
					break;
				}
			}
		}
	}

});