define([ "jquery", "i18n", "message-bus" ], function($, i18n, bus) {

	var dialog = null;
	var divContent = null;
	var layerIdLegend = [];

	var getDialog = function() {
		if (dialog == null) {
			dialog = $("<div/>");
			dialog.attr("title", "Legend");
			dialog.attr("id", "legend_pane");
			divContent = $("<div/>");
			divContent.appendTo(dialog);
			divContent.attr("id", "legend_pane_content");
			dialog.dialog({
				position : [ 'right', 'bottom' ],
				closeOnEscape : false,
				height : 300,
				minHeight : 400,
				maxHeight : 400,
				width : 430,
				zIndex : 2000,
				resizable : false
			});
		}

		return dialog;
	};

	var getDivContent = function() {
		if (divContent == null) {
			getDialog();
		}

		return divContent;
	};

	bus.listen("toggle-legend", function() {
		var dialog = getDialog();
		if (!dialog.dialog("isOpen")) {
			getDialog().dialog("open");
		} else {
			getDialog().dialog("close");
		}
	});

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.hasOwnProperty("legendURL")) {
			layerIdLegend[layerInfo.id] = layerInfo;
		}
	});

	bus.listen("layer-visibility", function(event, layerId, visibility) {
		var idPrefix = "legend_panel_";
		var layerInfo = layerIdLegend[layerId];
		if (layerInfo != undefined) {
			if (visibility) {
				var imagePath = layerInfo.legendURL;
				var tblLegend = $("<table/>").appendTo(getDivContent());
				tblLegend.attr("id", idPrefix + layerId);
				tblLegend.addClass("layer_legend");

				var trTitle = $("<tr/>").appendTo(tblLegend).addClass("legend_header");
				$("<td/>").appendTo(trTitle).addClass("legend_layer_name").html(layerInfo.name);
				if (layerInfo.hasOwnProperty("sourceLink") && layerInfo.hasOwnProperty("sourceLabel")) {
					var tdSourceLink = $("<td/>").appendTo(trTitle).addClass("data_source_link");
					$("<span/>").appendTo(tdSourceLink).addClass("lang").html(i18n["data_source"] + ":");
					$("<a/>").appendTo(tdSourceLink).attr("target", "_blank").attr("href", layerInfo.sourceLink).html(layerInfo.sourceLabel);
				}
				var trImage = $("<tr/>").appendTo(tblLegend).addClass("legend_image");
				var tdImage = $("<td/>").appendTo(trImage);
				$("<img/>").attr("src", imagePath).appendTo(tdImage);
			} else {
				$("#" + idPrefix + layerId).remove();
			}
		}
	});
});