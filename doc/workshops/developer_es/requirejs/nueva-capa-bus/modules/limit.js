define([ "jquery", "message-bus" ], function($, bus) {

	var layers = [];

	bus.listen("layer", function(e, info) {
		for ( var i = 0; i < layers.length; i++) {
			if (layers[i].url == info.url && layers[i].name == info.name) {
				window.alert("¡¡Capa duplicada!!");
				break;
			}
		}
		layers.push(info);
	});
});
