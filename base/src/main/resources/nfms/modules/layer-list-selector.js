define([ "jquery", "message-bus", "layout", "customization", "i18n", "jquery-ui" ], function($, bus, layout, customization, i18n, ui) {

	var divsById = [];
	
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

	var registerLayerPanel = function(id, priority, text, div) {
		var btn = $("<input type='radio'/>").attr("id", id).attr("name", "layerListSelector").appendTo(divLayerListSelector);
		var lbl = $("<label/>").addClass("noselect").attr("for", id).html(text).appendTo(divLayerListSelector);
				
		buttonPriorities.push({
			"btn" : btn,
			"lbl" : lbl,
			"priority" : priority,
			"div" : div
		});

		div.appendTo(divContainer);

		bus.listen("show-layer-panel", function(event, paneId) {
			if (paneId == id) {
				btn.prop("checked", true);
				btn.button("refresh");
				btn.change();
			}
		});

		// Workaround for http://bugs.jqueryui.com/ticket/7665
		lbl.click(function() {
			btn.checked = !btn.checked;
			btn.button("refresh");
			btn.change();
			bus.send("show-layer-panel", [ id ]);

			return false;
		});

		divsById[id] = div;
	};

	bus.listen("modules-loaded", function() {
		var byPriority = function(a, b) {
			return a.priority - b.priority;
		}
		buttonPriorities.sort(byPriority);
		for (var i = 0; i < buttonPriorities.length; i++) {
			if (i == 0) {
				buttonPriorities[i].btn.attr("checked", "true");
			} else {
				buttonPriorities[i].div.hide();
			}
			divLayerListSelector.append(buttonPriorities[i].btn);
			divLayerListSelector.append(buttonPriorities[i].lbl);
		}

		divLayerListSelector.buttonset();
		divLayerListSelector.show();
	});

	return {
		"registerLayerPanel" : registerLayerPanel
	};
});
