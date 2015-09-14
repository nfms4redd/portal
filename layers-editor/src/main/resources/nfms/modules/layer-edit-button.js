define([ "message-bus" ], function(bus) {

	function buildLink(type, id) {
		return $("<a/>")
			.addClass("layer_edit_button")
			.attr("id", "layer_edit_button_" + id)
			.click(function(event) {
				showForm(type, id);
			});
	};

	function showForm(type, id) {
		console.log("Edit " + type + " " + id);
	};

	bus.listen("before-adding-layers", function() {
		bus.send("register-layer-action", function(layer) {
			return buildLink("layer", layer.id);
		});
		bus.send("register-group-action", function(group) {
			return buildLink("group", group.id);
		});
	});
});
