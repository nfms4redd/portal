define([ "jquery", "message-bus", "layout", "jquery-ui", "fancy-box" ], function($, bus, layout) {

	var temporalLayers = new Array();

	var divLayers = null;

	// Should go in layout
	// var divLayerListSelector = $("<div/>").attr("id",
	// "layer_list_selector");
	//
	// var rdoAllLayers = $("<input/>").attr("id",
	// "all_layers").attr("type", "radio")//
	// .attr("name", "layer_list_selector").attr("checked", "checked");
	// var rdoAllLayersLabel = $("<label/>").attr("for",
	// "all_layers").html(i18n.layers);
	// rdoAllLayers.append(rdoAllLayersLabel);
	// divLayerListSelector.append(rdoAllLayers);
	//
	// var rdoSelectedLayers = $("<input/>").attr("id",
	// "active_layers").attr("type", "radio")//
	// .attr("name", "layer_list_selector");
	// var rdoSelectedLayersLabel = $("<label/>").attr("for",
	// "active_layers").html(i18n.selected_layers);
	// rdoSelectedLayers.append(rdoSelectedLayersLabel);
	// divLayerListSelector.append(rdoSelectedLayers);

	var divLayersContainer = layout.layers;

//	var divActiveLayers = $("<div/>").attr("id", "active_layers");
//	var h3Title = $("<h3/>").html("Selected layers");
//	divActiveLayers.append(h3Title);
//	divLayersContainer.append(divActiveLayers);

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
	divLayersContainer.append(divLayers);

	bus.listen("add-group", function(event, groupInfo) {
		var divTitle = $("<div/>").html(groupInfo.name).disableSelection();

		if (groupInfo.hasOwnProperty("infoLink")) {
			infoButton = $('<a style="position:absolute;top:3px;right:4px;width:16px;height:16px;padding:0;" class="layer_info_button" href="' + groupInfo.infoLink + '"></a>');

			// prevent accordion item from expanding when clicking on the info button
			infoButton.click(function (event) { event.stopPropagation() });

			infoButton.fancybox({
				'autoScale' : false,
				'openEffect' : 'elastic',
				'closeEffect' : 'elastic',
				'type': 'ajax',
				'overlayOpacity': 0.5
			});

			divTitle.append(infoButton);
		}


		var tblLayerGroup = $("<table/>");
		tblLayerGroup.attr("id", "group-content-table-" + groupInfo.id);
		
		if (groupInfo.hasOwnProperty("parentId")) {
			var parentId = groupInfo.parentId;
			var tblParentLayerGroup = $("#group-content-table-" + parentId);
			if (tblParentLayerGroup.length == 0) {
				bus.send("error", "Group " + groupInfo.name + " references nonexistent group: " + parentId);
			}
			tblParentLayerGroup.append(divTitle).append(tblLayerGroup);
		} else {
			divLayers.append(divTitle);
			var divContent = $("<div/>").css("padding", "10px 2px 10px 2px");
			divContent.append(tblLayerGroup);
			divLayers.append(divContent).accordion("refresh");
		}
	});

    bus.listen("add-portal-layer", function(event, portalLayer) {
        var tblLayerGroup = $("#group-content-table-" + portalLayer.groupId);
        if (tblLayerGroup.length == 0) {
            bus.send("error", "Layer " + portalLayer.label + " references nonexistent group: " + portalLayer.groupId);
        } else {
            var trLayer = $("<tr/>").attr("id", "layer-row-" + portalLayer.id).addClass("layer_row");

            var tdLegend = $("<td/>").addClass("layer_legend");
            trLayer.append(tdLegend);

            var tdVisibility = $("<td/>").css("width", "16px");
            var divCheckbox = $("<div/>").addClass("layer_visibility");
            if (portalLayer.active) {
                divCheckbox.addClass("checked");
            }
            divCheckbox.mousedown(function() {
                divCheckbox.addClass("mousedown");
            })
                .mouseup(function() {
                    divCheckbox.removeClass("mousedown");
                }).mouseleave(function() {
                    divCheckbox.removeClass("in");
                }).mouseenter(function() {
                    divCheckbox.addClass("in");
                }).click(function() {
                    divCheckbox.toggleClass("checked");
                    portalLayer.active = divCheckbox.hasClass("checked");
                    bus.send("portal-layer-visibility", portalLayer);
                });

            tdVisibility.append(divCheckbox);

            trLayer.append(tdVisibility);

            var tdName = $("<td/>").addClass("layer_name");
            tdName.html(portalLayer.label);
            trLayer.append(tdName);

            var tdInfo = $("<td/>").addClass("layer_info");
            if (portalLayer.hasOwnProperty("infoLink")) {
                var aLink = $("<a/>").attr("href", portalLayer.infoLink);
                aLink.addClass("layer_info_button");
                aLink.fancybox({
                    "closeBtn" : "true",
                    "openEffect" : "elastic",
                    "closeEffect" : "elastic",
                    "type" : "iframe",
                    "overlayOpacity" : 0.5
                });
                tdInfo.append(aLink);
            }
            trLayer.append(tdInfo);

            if (portalLayer.hasOwnProperty("timestamps")) {
                temporalLayers.push(portalLayer);
            }

            tblLayerGroup.append(trLayer);
            divLayers.accordion("refresh");
        }
    })

	bus.listen("add-layer", function(event, layerInfo) {
        return;

        alert('this message should show')
		var tblLayerGroup = $("#group-content-table-" + layerInfo.groupId);
		if (tblLayerGroup.length == 0) {
			bus.send("error", "Layer " + layerInfo.name + " references nonexistent group: " + layerInfo.groupId);
		} else {
			var trLayer = $("<tr/>").attr("id", "layer-row-" + layerInfo.id).addClass("layer_row");

			var tdLegend = $("<td/>").addClass("layer_legend");
			trLayer.append(tdLegend);

			var tdVisibility = $("<td/>").css("width", "16px");
			var divCheckbox = $("<div/>").addClass("layer_visibility");
			if (layerInfo.visible) {
				divCheckbox.addClass("checked");
			}
			divCheckbox.mousedown(function() {
				divCheckbox.addClass("mousedown");
			}).mouseup(function() {
				divCheckbox.removeClass("mousedown");
			}).mouseleave(function() {
				divCheckbox.removeClass("in");
			}).mouseenter(function() {
				divCheckbox.addClass("in");
			}).click(function() {
				divCheckbox.toggleClass("checked");
				var checked = divCheckbox.hasClass("checked");
				bus.send("layer-visibility", [ layerInfo, checked ]);
			});

			tdVisibility.append(divCheckbox);

			trLayer.append(tdVisibility);

			var tdName = $("<td/>").addClass("layer_name");
			tdName.html(layerInfo.name);
			trLayer.append(tdName);

			var tdInfo = $("<td/>").addClass("layer_info");
			if (layerInfo.hasOwnProperty("infoLink")) {
				var aLink = $("<a/>").attr("href", layerInfo.infoLink);
				aLink.addClass("layer_info_button");
				aLink.fancybox({
					"closeBtn" : "true",
					"openEffect" : "elastic",
					"closeEffect" : "elastic",
					"type" : "iframe",
					"overlayOpacity" : 0.5
				});
				tdInfo.append(aLink);
			}
			trLayer.append(tdInfo);

			if (layerInfo.hasOwnProperty("timestamps")) {
				temporalLayers.push(layerInfo);
			}

			tblLayerGroup.append(trLayer);
			divLayers.accordion("refresh");
		}
	});

	bus.listen("time-slider.selection", function(event, date) {
		for (var i = 0; i < temporalLayers.length; i++) {
			var layer = temporalLayers[i];
			var timestamps = layer.timestamps;
			var closestPrevious = null;
			timestamps.sort();
			for ( var j = 0; j < timestamps.length; j++) {
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

			var tdLayerName = $("#layer-row-" + layer.id + " .layer_name");
			tdLayerName.find("span").remove();
			$("<span/>").html(" (" + closestPrevious.getFullYear() + ")").appendTo(tdLayerName);

			bus.send("layer-timestamp-selected", [layer.id, closestPrevious]);
		}
	});

	bus.listen("show-layer-list", function(event, groupInfo) {
		divLayersContainer.show();
	});
	bus.listen("hide-layer-list", function(event, groupInfo) {
		divLayersContainer.hide();
	});

	return divLayersContainer;
});
