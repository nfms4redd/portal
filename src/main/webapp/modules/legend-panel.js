define([ "jquery", "i18n", "customization", "message-bus" ], function($, i18n, customization, bus) {

	var dialog = null;
	var divContent = null;

	var getDialog = function() {
		if (dialog == null) {
			dialog = $("<div/>");
			dialog.attr("title", i18n["legend_button"]);
			dialog.attr("id", "legend_pane");
			divContent = $("<div/>");
			divContent.appendTo(dialog);
			divContent.attr("id", "legend_pane_content");
			dialog.dialog({
				position : [ 'right', 'bottom' ],
				closeOnEscape : false,
				autoOpen : false,
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

	bus.listen("open-legend", function(event, layerId) {
		getDialog().dialog("open");
		var table = $("#legend_panel_" + layerId);
		var dialog = getDialog();
		dialog.animate({
			scrollTop : table.offset().top - dialog.offset().top + dialog.scrollTop()
		});
	});

	bus.listen("toggle-legend", function() {
		var dialog = getDialog();
		if (!dialog.dialog("isOpen")) {
			getDialog().dialog("open");
		} else {
			getDialog().dialog("close");
		}
	});

	bus.listen("layer-visibility", function(event, layerInfo, visibility) {
		var idPrefix, imagePath, tblLegend;

		idPrefix = "legend_panel_";
		if (layerInfo.hasOwnProperty("legend")) {
			if (visibility) {
				imagePath = "static/loc/" + customization.languageCode + "/images/" + layerInfo.legend;
				tblLegend = $("<table/>").appendTo(getDivContent());
				tblLegend.attr("id", idPrefix + layerInfo.id);
				tblLegend.addClass("layer_legend");

				var trTitle = $("<tr/>").appendTo(tblLegend).addClass("legend_header");
				$("<td/>").appendTo(trTitle).addClass("legend_layer_name").html(layerInfo.label);
				if (layerInfo.hasOwnProperty("sourceLink") && layerInfo.hasOwnProperty("sourceLabel")) {
					var tdSourceLink = $("<td/>").appendTo(trTitle).addClass("data_source_link");
					$("<span/>").appendTo(tdSourceLink).addClass("lang").html(i18n["data_source"] + ":");
					$("<a/>").appendTo(tdSourceLink).attr("target", "_blank").attr("href", layerInfo.sourceLink).html(layerInfo.sourceLabel);
				}
				var trImage = $("<tr/>").appendTo(tblLegend).addClass("legend_image");
				var tdImage = $("<td/>").appendTo(trImage);
				$("<img/>").attr("src", imagePath).appendTo(tdImage);
			} else {
				$("#" + idPrefix + layerInfo.id).remove();
			}
		}
	});
});