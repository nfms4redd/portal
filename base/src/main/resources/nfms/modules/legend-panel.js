define([ "jquery", "i18n", "customization", "message-bus" ], function($, i18n, customization, bus) {

	/*
	 * keep the information about layer legends that will be necessary when they
	 * become visible
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
				position : {
					my : "right top",
					at : "right bottom+15",
					of : "#toggle_legend"
				},
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

	var refreshLegendArray = function(legendArray) {
		var idPrefix = "legend_panel_";
		for (var i = 0; i < legendArray.length; i++) {
			var legendInfo = legendArray[i];
			if (legendInfo.visibility) {
				var id = idPrefix + legendInfo.id;
				var img = $("#" + id + "_img");
				if (img.length == 0) {
					var tblLegend = $("<table/>").appendTo(getDivContent());
					tblLegend.attr("id", id);
					tblLegend.addClass("layer_legend");

					var trTitle = $("<tr/>").appendTo(tblLegend).addClass("legend_header");
					$("<td/>").appendTo(trTitle).addClass("legend_layer_name").html(legendInfo.label);
					if (typeof legendInfo["sourceLink"] != "undefined" && typeof legendInfo["sourceLabel"] != "undefined") {
						var tdSourceLink = $("<td/>").appendTo(trTitle).addClass("data_source_link");
						$("<span/>").appendTo(tdSourceLink).addClass("lang").html(i18n["data_source"] + ":");
						$("<a/>").appendTo(tdSourceLink).attr("target", "_blank").attr("href", legendInfo.sourceLink).html(legendInfo.sourceLabel);
					}
					var trImage = $("<tr/>").appendTo(tblLegend).addClass("legend_image");
					var tdImage = $("<td/>").attr("colspan", "2").appendTo(trImage);
					img = $("<img/>").attr("id", id + "_img").appendTo(tdImage);
				}
				var url = legendInfo.legendUrl;
				if (legendInfo.timeDependent && legendInfo.timestamp) {
					url = url + "&STYLE=" + legendInfo.timestyle + "&TIME=" + legendInfo.timestamp.toISO8601String();
				}
				img.attr("src", url);
			} else {
				$("#" + idPrefix + legendInfo.id).remove();
			}
		}
	}

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

	bus.listen("reset-layers", function() {
		legendArrayInfo = {};
	});

	bus.listen("add-layer", function(event, layerInfo) {
		var legendArray = [];
		$.each(layerInfo.getMapLayers(), function(index, mapLayer) {
			if (mapLayer.hasOwnProperty("legend")) {
				legendArray.push({
					id : mapLayer.getId(),
					label : mapLayer.getName(),
					legendUrl : mapLayer.getLegendURL(),
					sourceLink : mapLayer.getSourceLink(),
					sourceLabel : mapLayer.getSourceLabel(),
					visibility : layerInfo.isActive(),
					timeDependent : layerInfo.hasTimeDependentStyle()
				});
			}
		});
		if (legendArray.length > 0) {
			legendArrayInfo[layerInfo.getId()] = legendArray;
		}
	});

	bus.listen("layer-timestamp-selected", function(e, layerId, d, style) {
		var legendArray = legendArrayInfo[layerId];
		if (legendArray) {
			$.each(legendArray, function(index, legendInfo) {
				if (legendInfo.timeDependent) {
					legendInfo["timestamp"] = d;
					legendInfo["timestyle"] = style
				}
			});

			refreshLegendArray(legendArray);
		}
	});

	bus.listen("layer-visibility", function(event, layerId, visibility) {
		var legendArray = legendArrayInfo[layerId] || [];
		$.each(legendArray, function(index, legendInfo) {
			legendInfo["visibility"] = visibility;
		});

		refreshLegendArray(legendArray);
	});

});