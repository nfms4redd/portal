define([ "message-bus", "layers-edit-form", "jquery", "jquery-ui" ], function(bus, forms, $) {

	var layerRoot = null

	bus.listen("layers-loaded", function(e, newLayerRoot) {
		layerRoot = newLayerRoot;
	});

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
				layer.remove();
			});
		});
		bus.send("register-group-action", function(group) {
			return $("<a/>").addClass("editable-layer-list-button").addClass("layer_deleteGroup_button").click(function() {
				group.remove();
			});
		});
	});

	function link(id, callback) {
		return $("<a/>").addClass("editable-layer-list-button").addClass("layer_edit_button").click(function() {
			callback.call(null, id);
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
			var groupInfo = layerRoot.getGroup($(el).attr("data-group"));
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

				var groupId = ui.item.attr("data-group");
				var newPosition = ui.item.index();
				var currentAncestor = ui.item.parent();
				while (currentAncestor.attr("id") != "all_layers" && currentAncestor.attr('data-group') == undefined) {
					currentAncestor = currentAncestor.parent();
				}
				var parentDiv = currentAncestor.attr("id") == "all_layers" ? null : currentAncestor.attr("data-group");

				layerRoot.moveGroup(groupId, parentDiv, newPosition);
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
				var layerId = ui.item.attr("data-layer");
				var newPosition = ui.item.index();
				var currentAncestor = ui.item.parent();
				while (currentAncestor.attr("id") != "all_layers" && currentAncestor.attr('data-group') == undefined) {
					currentAncestor = currentAncestor.parent();
				}
				var parentId = currentAncestor.attr("id") == "all_layers" ? null : currentAncestor.attr("data-group");
				layerRoot.moveLayer(layerId, parentId, newPosition);
			}
		});
	});

});
