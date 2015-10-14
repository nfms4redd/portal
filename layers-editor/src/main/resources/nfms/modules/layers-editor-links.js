define([ "message-bus", "layers-editor-forms" ], function(bus, forms) {

	bus.listen("before-adding-layers", function() {
		bus.send("register-layer-action", function(layer) {
			return link(layer.id, forms.editLayer);
		});
		bus.send("register-group-action", function(group) {
			return link(group.id, forms.editGroup);
		});
	});

	function link(id, callback) {
		return $("<a/>").addClass("layer_edit_button").click(function() {
			callback.call(null, id);
		});
	}

});
