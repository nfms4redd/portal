define([ "jquery", "i18n", "customization", "message-bus" ], function($, i18n, customization, bus) {

	/*
	 * keep the information about layer legends that will be necessary when
	 * they become visible
	 */
	var legendArrayInfo = {};

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
				position : [ 'right', 'center' ],
				closeOnEscape : false,
				autoOpen : false,
				height : 300,
				minHeight : 400,
				maxHeight : 400,
				width : 325,
				zIndex : 2000,
				resizable : true
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

	bus.listen("add-layer", function(event, layerInfo) {
		var legendArray = [];
		$.each(layerInfo.wmsLayers, function(index, wmsLayer) {
			if (wmsLayer.hasOwnProperty("legend")) {
				legendArray.push({
					id : wmsLayer.id,
					legend : wmsLayer.legend,
					label : wmsLayer.label,
					sourceLink : wmsLayer.sourceLink,
					sourceLabel : wmsLayer.sourceLabel
				});
			}
		});
		if (legendArray.length > 0) {
			legendArrayInfo[layerInfo.id] = legendArray;
		}
	});

	bus.listen("layer-visibility", function(event, layerId, visibility) {
		var idPrefix, imagePath, tblLegend;

		idPrefix = "legend_panel_";
		var legendArray = legendArrayInfo[layerId] || [];
		for (var i = 0; i < legendArray.length; i++) {
			var legendInfo = legendArray[i];
			if (visibility) {
				imagePath = "static/loc/" + customization.languageCode + "/images/" + legendInfo.legend;
				tblLegend = $("<table/>").appendTo(getDivContent());
				tblLegend.attr("id", idPrefix + legendInfo.id);
				tblLegend.addClass("layer_legend");

				var trTitle = $("<tr/>").appendTo(tblLegend).addClass("legend_header");
				$("<td/>").appendTo(trTitle).addClass("legend_layer_name").html(legendInfo.label);
				if (typeof legendInfo["sourceLink"] != "undefined" && typeof legendInfo["sourceLabel"] != "undefined") {
					var tdSourceLink = $("<td/>").appendTo(trTitle).addClass("data_source_link");
					$("<span/>").appendTo(tdSourceLink).addClass("lang").html(i18n["data_source"] + ":");
					$("<a/>").appendTo(tdSourceLink).attr("target", "_blank").attr("href", legendInfo.sourceLink).html(legendInfo.sourceLabel);
				}
				var trImage = $("<tr/>").appendTo(tblLegend).addClass("legend_image");
				var tdImage = $("<td/>").appendTo(trImage);
				$("<img/>").attr("src", imagePath).appendTo(tdImage);
			} else {
				$("#" + idPrefix + legendInfo.id).remove();
			}
		}
	});
});