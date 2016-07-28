define([ "jquery", "message-bus", "layout", "customization", "i18n", "jquery-ui" ], function($, bus, layout, customization, i18n, ui) {

	var divsById = {};
	var buttonPriorities = [];
	
	var divContainer = $("<div/>").attr("id", "layers_container").appendTo("body");
	var divLayerListSelector = $("<div/>").attr("id", "layer_list_selector_pane").appendTo("body").hide();

	bus.listen("show-layer-panel", function(event, id) {
		for (divId in divsById) {
			if (divId == id) {
				divsById[divId].show();
			} else {
				divsById[divId].hide();
			}
		}
	});

	bus.listen("show-layer-panel", function(event, paneId) {
		var btn = $("#" + paneId);
		btn.prop("checked", true);
		btn.button("refresh");
		btn.change();
	});

	var registerLayerPanel = function(id, priority, text, div) {
		var btn = $("<input type='radio'/>").attr("id", id).attr("name", "layerListSelector").appendTo(divLayerListSelector);
		var lbl = $("<label/>").addClass("noselect").attr("for", id).html(text).appendTo(divLayerListSelector);
				
		buttonPriorities.push({
			"id": id,
			"btn" : btn,
			"lbl" : lbl,
			"priority" : priority,
			"div" : div
		});

		div.appendTo(divContainer);

		divsById[id] = div;
		renderButtons();
	};

	var removeLayerPanel = function(id) {
		$("#" + id).remove();
		$("label[for=" + id + "]").remove();
		for(var i in buttonPriorities) {
			if(buttonPriorities[i].id == id) {
				// Remove div
				buttonPriorities[i].div.remove();
				// Remove from buttonPriorities array
				buttonPriorities.splice(i, 1);
				break;
			}
		}
		// Remove from divsById
		delete divsById[id];
		renderButtons();
	};

	var renderButtons = function() {
		divLayerListSelector.empty();
		var byPriority = function(a, b) {
			return a.priority - b.priority;
		}
		buttonPriorities.sort(byPriority);
		for (var i = 0; i < buttonPriorities.length; i++) {
			var bp = buttonPriorities[i];
			if (i == 0) {
				bp.btn.attr("checked", "true");
			} else {
				bp.div.hide();
			}
			divLayerListSelector.append(bp.btn);
			divLayerListSelector.append(bp.lbl);

			bp.lbl.on("click", bp, function(event) {
				var bp = event.data;
				bp.btn.checked = !bp.btn.checked;
				bp.btn.button("refresh");
				bp.btn.change();
				bus.send("show-layer-panel", [bp.id]);
				console.log(bp.id);
				return false;
			});
		}
		divLayerListSelector.buttonset();
		divLayerListSelector.show();
	}

	bus.listen("modules-loaded", function() {
		renderButtons();
	});

	return {
		"registerLayerPanel" : registerLayerPanel,
		"removeLayerPanel" : removeLayerPanel
	};
});
