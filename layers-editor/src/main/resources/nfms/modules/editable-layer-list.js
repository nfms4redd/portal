define(["message-bus", "layers-edit-form", "layers", "layers-json", "jquery", "jquery-ui"], function(bus, forms, layers, layers_json, $) {

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

	bus.listen("layers-loaded", function() {
		//var add = $("<div/>").addClass("root_group_placeholder").append("Add Group ").append($("<div/>").addClass("fa fa-plus")); // TODO i18n
		//$("#layers_container").append(add);
		$(".group-container").sortable({
			handle: ".group-title",
			connectWith: ".group-container",
			axis: "y",
			cursor: "move",
			placeholder: "root_group_placeholder",
			forcePlaceholderSize: true,
			stop: function(event, ui) {
				// IE doesn't register the blur when sorting
				// so trigger focusout handlers to remove .ui-state-focus
				ui.item.children(".ui-accordion-header").triggerHandler("focusout");
				// Refresh accordion to handle new order
				$("#all_layers > div").each(function() {
					$(this).accordion("refresh");
				});

				// Read new group hierarchy from DOM and save it to the server
				var groups = getGroups($("#all_layers")).items;
				layers_json.updateGroups(groups, function() {
					console.log("New group order saved");
					layers.clear();
					layers.draw(layers_json.root);
				});
			}
		});

		$("#all_layers").accordion("destroy");
		$("#all_layers > div").accordion({
			animate: false,
			header: " > div.group-title",
			heightStyle: "content",
			collapsible: true,
			active: false
		});

		$(".layer-container").sortable({
			connectWith: ".layer-container",
			axis: "y",
			cursor: "move",
			placeholder: "root_group_placeholder",
			forcePlaceholderSize: true,
			stop: function(event, ui) {
				// IE doesn't register the blur when sorting
				// so trigger focusout handlers to remove .ui-state-focus
				ui.item.children(".ui-accordion-header").triggerHandler("focusout");
				// Refresh accordion to handle new order
				$("#all_layers > div").each(function() {
					$(this).accordion("refresh");
				});
				// Read new group hierarchy from DOM and save it to the server
				var groups = getGroups($("#all_layers")).items;
				layers_json.updateGroups(groups, function() {
					console.log("New group order saved");
				});
			}
		});
	});

	bus.listen("remove-layer", function(event, layer) {
		console.log("Remove layer");
		console.log(layer);
	});

	bus.listen("remove-group", function(event, group) {
		console.log("Remove group");
		console.log(group);
	});

	bus.listen("layers-removed", function() {
		console.log("All layers removed");
	});

	function getGroups(el) {
		var attrs = el.attr("data-group") ? JSON.parse(el.attr("data-group")) : {};
		if(attrs.hasOwnProperty("name")) {
			attrs.label = attrs.name;
		}
		delete attrs.name;
		delete attrs.parentId;
		attrs.items = [];
		$(el.find(".group")[0]).parent().children(".group").each(function(i) {
			attrs.items.push(getGroups($(this)));
		});
		attrs.items = attrs.items.concat(getLayers(el));
		return attrs;
	}

	function getLayers(el) {
		var ids = [];
		el.find(".layer_row").filter(function() {
			return $(this).parentsUntil(el,'.group').length < 1;
		}).each(function(i) {
			var id = $(this).attr('id').replace('layer-row-','');
			ids.push(id);
		});
		return ids;
	}

});
