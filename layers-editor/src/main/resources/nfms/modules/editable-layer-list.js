define([ "message-bus", "layers-edit-form", "layers", "layers-json", "jquery", "jquery-ui" ], function(bus, forms, layers, layers_json, $) {

	bus.listen("before-adding-layers", function() {
		bus.send("register-layer-action", function(layer) {
			return link(layer.getId(), forms.editLayer);
		});
		bus.send("register-group-action", function(group) {
			return link(group.getId(), forms.editGroup);
		});
		bus.send("register-group-action", function(group) {
			return $("<a/>").addClass("editable-layer-list-button").addClass("layer_newLayer_button").click(function() {
				forms.newLayer(group.getId());
			});
		});
		bus.send("register-layer-action", function(layer) {
			return $("<a/>").addClass("editable-layer-list-button").addClass("layer_deleteLayer_button").click(function() {
				layers_json.deleteLayer(layer.getId(), function() {
					layers.redraw(layers_json.root);
				});
			});
		});
		bus.send("register-group-action", function(group) {
			return $("<a/>").addClass("editable-layer-list-button").addClass("layer_deleteGroup_button").click(function() {
				layers_json.deleteGroup(group.getId(), function() {
					layers.redraw(layers_json.root);
				});
			});
		});
	});

	function link(id, callback) {
		return $("<a/>").addClass("editable-layer-list-button").addClass("layer_edit_button").click(function() {
			callback.call(null, id);
		});
	}

	function save() {
		var groups = getGroups($("#all_layers")).items;
		layers_json.updateGroups(groups, function() {
			console.log("New group order saved");
			layers.redraw(layers_json.root);
		});
	}

	bus.listen("layers-loaded", function() {

		$("#newGroupButton").remove();
		$("<div/>")//
		.attr("id", "newGroupButton")//
		.css("margin-top", "2px")//
		.html("Nuevo grupo...")//
		.button()//
		.click(function() {
			forms.newGroup();
		})//
		.appendTo($("#layers_container"));//

		// var add = $("<div/>").addClass("root_group_placeholder").append("Add
		// Group ").append($("<div/>").addClass("fa fa-plus")); // TODO i18n
		// $("#layers_container").append(add);

		// Añadir placeholder para soltar subgrupos
		$(".group").each(function(i, el) {
			var groupInfo = getGroups($(el));
			if (groupInfo.items.length == 0) { // Sólo en grupos vacíos
				$("<div/>").addClass("group_placeholder").addClass("group-container").appendTo($("#group-content-table-" + groupInfo.id));
			}
		});

		// Añadir placeholder para soltar capas
		$("<tr/>").addClass("layer_placeholder").appendTo(".layer-container");

		$(".group-container").sortable({
			handle : ".group-title",
			connectWith : ".group-container",
			axis : "y",
			cursor : "move",
			placeholder : "root_group_placeholder",
			forcePlaceholderSize : true,
			stop : function(event, ui) {
				// IE doesn't register the blur when sorting
				// so trigger focusout handlers to remove .ui-state-focus
				ui.item.children(".ui-accordion-header").triggerHandler("focusout");
				// Refresh accordion to handle new order
				$("#all_layers > div").each(function() {
					if ($(this).hasClass("ui-accordion")) {
						$(this).accordion("refresh");
					}
				});

				save();
			}
		});

		$("#all_layers").accordion("destroy");
		$("#all_layers > div").accordion({
			animate : false,
			header : " > div.group-title",
			heightStyle : "content",
			collapsible : true,
			active : false
		});

		$(".layer-container").sortable({
			connectWith : ".layer-container",
			axis : "y",
			cursor : "move",
			placeholder : "root_group_placeholder",
			forcePlaceholderSize : true,
			stop : function(event, ui) {
				save();
			}
		});
	});

	function getGroups(el) {
		var attrs = el.attr("data-group") ? JSON.parse(el.attr("data-group")) : {};
		if (attrs.hasOwnProperty("name")) {
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
			return $(this).parentsUntil(el, '.group').length < 1;
		}).each(function(i) {
			var id = $(this).attr('id').replace('layer-row-', '');
			ids.push(id);
		});
		return ids;
	}

});
