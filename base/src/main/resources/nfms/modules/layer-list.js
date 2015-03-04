define([ "jquery", "message-bus", "layer-list-selector", "i18n", "jquery-ui", "fancy-box" ], function($, bus, layerListSelector, i18n) {

	var layerActions = new Array();

	var temporalLayers = new Array();

	var divLayers = null;

	bus.listen("register-layer-action", function (event, action) {
		layerActions.push(action);
	});

	divLayers = $("<div/>").attr("id", "all_layers");
	divLayers.addClass("ui-accordion-icons");
	divLayers.accordion({
		"animate" : false,
		"heightStyle" : "content",
		/*
		 * Collapse all content since otherwise the accordion sets the 'display'
		 * to 'block' instead than to 'table'
		 */
		"collapsible" : true,
		"active" : false
	});
	layerListSelector.registerLayerPanel("all_layers_selector", i18n.layers, divLayers);

	bus.listen("add-group", function(event, groupInfo) {
		var divTitle, tblLayerGroup, parentId, tblParentLayerGroup, divContent;

		divTitle = $("<div/>").html(groupInfo.name).disableSelection();

		if (groupInfo.hasOwnProperty("infoLink")) {
			infoButton = $('<a style="position:absolute;top:3px;right:4px;width:16px;height:16px;padding:0;" class="layer_info_button" href="' + groupInfo.infoLink + '"></a>');

			// prevent accordion item from expanding
			// when clicking on the info button
			infoButton.click(function(event) {
				event.stopPropagation()
			});

			infoButton.fancybox({
				'autoScale' : false,
				'openEffect' : 'elastic',
				'closeEffect' : 'elastic',
				'type' : 'ajax',
				'overlayOpacity' : 0.5
			});

			divTitle.append(infoButton);
		}

		tblLayerGroup = $("<table/>");
		tblLayerGroup.attr("id", "group-content-table-" + groupInfo.id);

		if (groupInfo.hasOwnProperty("parentId")) {
			parentId = groupInfo.parentId;
			tblParentLayerGroup = $("#group-content-table-" + parentId);
			if (tblParentLayerGroup.length == 0) {
				bus.send("error", "Group " + groupInfo.name + " references nonexistent group: " + parentId);
			}
			tblParentLayerGroup.append(divTitle).append(tblLayerGroup);
		} else {
			divLayers.append(divTitle);
			divContent = $("<div/>").css("padding", "10px 2px 10px 2px");
			divContent.append(tblLayerGroup);
			divLayers.append(divContent).accordion("refresh");
		}
	});

	bus.listen("add-layer", function(event, portalLayer) {
		var tblLayerGroup, trLayer, tdLegend, tdVisibility, divCheckbox, tdName, tdInfo, aLink, inlineLegend;

		tblLayerGroup = $("#group-content-table-" + portalLayer.groupId);
		if (tblLayerGroup.length == 0) {
			bus.send("error", "Layer " + portalLayer.label + " references nonexistent group: " + portalLayer.groupId);
		} else {
			trLayer = $("<tr/>").attr("id", "layer-row-" + portalLayer.id).addClass("layer_row");

			tdLegend = $("<td/>").addClass("layer_legend");

			if (portalLayer.hasOwnProperty("inlineLegendUrl")) {
				// context has an inline legend
				// tdLegend = $('<td
				// style="width:20px">');
				// inlineLegend = $('<img
				// class="inline-legend" src="' +
				// UNREDD.wmsServers[0] +
				// contextConf.inlineLegendUrl + '">');
				inlineLegend = $('<img class="inline-legend" src="' + portalLayer.inlineLegendUrl + '">');
				tdLegend.append(inlineLegend);
			} else {
				var wmsLayersWithLegend = portalLayer.wmsLayers.filter(function(layer) {
					return layer.hasOwnProperty("legend");
				});
				var wmsLayerWithLegend = wmsLayersWithLegend[0];

				if (wmsLayerWithLegend) {
					inlineLegend = $("<td/>");
					inlineLegend.addClass("inline-legend-button");

					if (portalLayer.active) {
						inlineLegend.addClass("visible");
					}

					bus.listen("layer-visibility", function(event, layerId, visibility) {
						if (layerId != portalLayer.id) {
							return;
						}

						if (visibility) {
							inlineLegend.addClass("visible");
						} else {
							inlineLegend.removeClass("visible");
						}
					});

					inlineLegend.click(function() {
						if ($("#" + portalLayer.id + "_visibility_checkbox").hasClass("checked")) {
							bus.send("open-legend", wmsLayerWithLegend.id);
						}
					});

					tdLegend.append(inlineLegend);
				}
			}
			trLayer.append(tdLegend);

			tdVisibility = $("<td/>").css("width", "16px");
			divCheckbox = $("<div/>").attr("id", portalLayer.id + "_visibility_checkbox").addClass("layer_visibility");
			if (portalLayer.active) {
				divCheckbox.addClass("checked");
			}
			divCheckbox.mousedown(function() {
				divCheckbox.addClass("mousedown");
			}).mouseup(function() {
				divCheckbox.removeClass("mousedown");
			}).mouseenter(function() {
				divCheckbox.addClass("in");
			}).mouseleave(function() {
				divCheckbox.removeClass("in");
			}).click(function() {
				divCheckbox.toggleClass("checked");
				bus.send("layer-visibility", [ portalLayer.id, divCheckbox.hasClass("checked") ]);
			});

			if (!portalLayer.isPlaceholder) {
				tdVisibility.append(divCheckbox);
			}

			trLayer.append(tdVisibility);

			tdName = $("<td/>").addClass("layer_name");
			tdName.html(portalLayer.label);
			trLayer.append(tdName);

			for (var i = 0; i < layerActions.length; i++) {
				var layerAction = layerActions[i];
				var element = layerAction(portalLayer);
				tdAction = $("<td/>").addClass("layer_action").appendTo(trLayer);
				if (element != null) {
					tdAction.append(element);
				}
			}

			if (portalLayer.hasOwnProperty("timestamps")) {
				temporalLayers.push(portalLayer);
			}

			tblLayerGroup.append(trLayer);
			divLayers.accordion("refresh");
		}
	});

	bus.listen("layer-visibility", function(event, layerId, visible) {
		var divCheckbox = $("#" + layerId + "_visibility_checkbox");
		if (visible) {
			divCheckbox.addClass("checked");
		} else {
			divCheckbox.removeClass("checked");
		}
	});

	var updateLabel = function(layerId, date) {
		var tdLayerName = $("#layer-row-" + layerId + " .layer_name");
		tdLayerName.find("span").remove();
		$("<span/>").html(" (" + date.getUTCFullYear() + ")").appendTo(tdLayerName);
	};
	
	bus.listen("time-slider.selection", function(event, date) {
		for (var i = 0; i < temporalLayers.length; i++) {
			var layer = temporalLayers[i];
			var timestamps = layer.timestamps;
			var closestPrevious = null;
			timestamps.sort();
			for (var j = 0; j < timestamps.length; j++) {
				var timestampString = timestamps[j];
				var timestamp = new Date();
				timestamp.setISO8601(timestampString);
				if (timestamp <= date) {
					closestPrevious = timestamp;
				} else {
					break;
				}
			}

			if (closestPrevious == null) {
				closestPrevious = new Date();
				closestPrevious.setISO8601(timestamps[0]);
			}

			updateLabel(layer.id, closestPrevious);
			
			bus.send("layer-timestamp-selected", [ layer.id, closestPrevious ]);
		}
	});
	bus.listen("layer-time-slider.selection", function(event, layerid, date) {
		updateLabel(layerid, date);
		bus.send("layer-timestamp-selected", [ layerid, date ]);
	});
});
