define([ "jquery", "fancy-box" ], function($) {

	var divLayers = null;

	$(document).trigger("css-load", "modules/layer-list.css");

	$(document).bind("init-layerlist", function(event, divLayersContainer) {
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

		var divActiveLayers = $("<div/>").attr("id", "active_layers");
		var h3Title = $("<h3/>").html("Selected layers");
		divActiveLayers.append(h3Title);
		divLayersContainer.append(divActiveLayers);

		divLayers = $("<div/>").attr("id", "all_layers");
		divLayers.addClass("ui-accordion-icons");
		divLayers.accordion({
			"animate" : false,
			"collapsible" : true
		});
		divLayersContainer.append(divLayers);
	});
	$(document).bind("add-group", function(event, groupInfo) {
		var divTitle = $("<div/>");
		aTitle = $("<a/>").attr("href", "#").html(groupInfo.name).disableSelection();
		divTitle.append(aTitle);
		divLayers.append(divTitle);
		var tblLayerGroup = $("<table/>");
		tblLayerGroup.attr("id", "group-content-table-" + groupInfo.id);
		tblLayerGroup.addClass("group-content-table");
		divLayers.append(tblLayerGroup).accordion("refresh");
	});
	$(document).bind("add-layer", function(event, layerInfo) {
		var tblLayerGroup = $("#group-content-table-" + layerInfo.groupId);
		if (tblLayerGroup.length == 0) {
			$(document).trigger("error", "Layer " + layerInfo.name + " references nonexistent group: " + layerInfo.groupId);
		} else {
			var trLayer = $("<tr/>").addClass("layer_row");

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
				$(document).trigger("layer-visibility", [ layerInfo.id, checked ]);
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

			tblLayerGroup.append(trLayer);
		}
	});

});