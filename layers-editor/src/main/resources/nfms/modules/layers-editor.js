define(["message-bus", "layers-json"], function(bus, layers) {

	bus.listen("before-adding-layers", function() {
		bus.send("register-layer-action", function(layer) {
			return link(layer.id, editLayer);
		});
		bus.send("register-group-action", function(group) {
			return link(group.id, editGroup);
		});
	});

	function link(id, onClick) {
		return $("<a/>")
			.addClass("layer_edit_button")
			.click(function() {
				onClick.call(null, id);
			});
	}

	function editLayer(id) {
		var portalLayer = layers.getPortalLayer(id);
		var wmsLayer = layers.getWmsLayer(portalLayer.layers[0]);
		alert("Edit Layer: " + JSON.stringify({
				portalLayer: portalLayer,
				wmsLayer: wmsLayer
			}, null, 3));
	}

	function editGroup(id) {
		var group = layers.getGroup(id);
		alert("Edit Group: " + group.label);
	}

});
