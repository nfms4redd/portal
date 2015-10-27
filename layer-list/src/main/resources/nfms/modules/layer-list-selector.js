define([ "jquery", "message-bus", "layout", "customization", "i18n", "jquery-ui" ], function($, bus, layout, customization, i18n, ui) {
	var divsById = [];
	var buttonPriorities = [];

	var container = $("<div/>").attr("id", "layer_list_selector_container");
	container.appendTo("body");

	var selector = $("<div/>").attr("id", "layer_list_selector");
	selector.appendTo(container);
	selector.hide();

	var content = $("<div/>").attr("id", "layer_list_content");
	content.appendTo(container);

	bus.listen("show-layer-panel", function(event, id) {
		for (divId in divsById) {
			if (divId == id) {
				divsById[divId].show();
			} else {
				divsById[divId].hide();
			}
		}
	});

	bus.listen("register-layer-panel", function(e, msg) {
		var id = msg.id;
		var div = msg.div;

		var btn = $("<input type='radio'/>").attr("id", id);
		btn.attr("name", "layerListSelector");
		btn.appendTo(selector);

		var lbl = $("<label/>").attr("for", id);
		lbl.addClass("noselect");
		lbl.html(msg.text);
		lbl.appendTo(selector);

		buttonPriorities.push({
			"btn" : btn,
			"lbl" : lbl,
			"priority" : msg.priority,
			"div" : div
		});

		div.addClass("layer_list_selector_div");
		div.appendTo(content);

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
	});

	bus.listen("modules-loaded", function() {
		if (buttonPriorities.length < 2) {
			// If there is only one panel (or zero), don't sort and don't show
			// the selector
			return;
		}

		buttonPriorities.sort(function(a, b) {
			return a.priority - b.priority;
		});

		for (var i = 0; i < buttonPriorities.length; i++) {
			if (i == 0) {
				buttonPriorities[i].btn.attr("checked", "true");
			} else {
				buttonPriorities[i].div.hide();
			}
			selector.append(buttonPriorities[i].btn);
			selector.append(buttonPriorities[i].lbl);
		}

		selector.buttonset();
		selector.show();
	});
});
