define([ "jquery", "message-bus", "layout", "map", "layer-list-selector", "moment", "jquery-ui" ], function($, bus, layout, map, layerListSelector, moment) {
	var aTimestampsLayers = {};
	var divTimeSliders = $("<div/>").attr("id", "layerTimeSliders").addClass("layer_container_panel");
	layerListSelector.registerLayerPanel("layer_slider_selector", 30, "Temporal", divTimeSliders);

	var formatDate = function(date, dateFormat) {
		var format;
		if (dateFormat) {
			format = dateFormat;
		} else {
			format = "YYYY";
		}
		return moment(date).format(format);
	}

	bus.listen("reset-layers", function() {
		$(".layer-time-slider").slider("destroy");
		$(".layer-time-slider-container").remove();
		aTimestampsLayers = {};
	});

	bus.listen("add-layer", function(event, layerInfo) {
		var timestamps = [];
		var layerTimestamps = layerInfo.getTimestamps();
		if (layerTimestamps != null) {
			for (var i = 0; i < layerTimestamps.length; i++) {
				var d = new Date();
				d.setISO8601(layerTimestamps[i]);
				timestamps.push(d);
			}
		}

		if (timestamps.length > 0) {
			timestamps.sort(function(a, b) {
				return a - b;
			});

			var divTimeSliderContainer = $("<div id='layer_time_slider_container_" + layerInfo.getId() + "'/>").addClass("layer-time-slider-container").appendTo(divTimeSliders);
			var divTimeSliderTitle = $("<div/>").html(layerInfo.getName()).addClass("layer-time-slider-title").appendTo(divTimeSliderContainer);
			var divTimeSliderLabel = $("<span id='layer_time_slider_label_" + layerInfo.getId() + "'/>").appendTo(divTimeSliderContainer);
			var divTimeSlider = $("<div id='layer_time_slider_" + layerInfo.getId() + "' class='layers_time_slider' />").appendTo(divTimeSliderContainer);
			divTimeSlider.addClass("layer-time-slider");

			divTimeSlider.slider({
				change : function(event, ui) {
					if (event.originalEvent) {
						var date = timestamps[ui.value];
						$.each(layerInfo.getMapLayers(), function(index, mapLayer) {
							var layer = map.getLayer(mapLayer.getId());
							layer.mergeNewParams({
								'time' : date.toISO8601String()
							});
							bus.send("layer-time-slider.selection", [ layerInfo.getId(), date ]);
						});
					} else { // Programatic change
						// alert('programatic');
					}
					;

				},
				slide : function(event, ui) {
					var date = timestamps[ui.value];
					divTimeSliderLabel.text(formatDate(date, layerInfo.getDateFormat()));
				},
				max : timestamps.length - 1,
				value : timestamps.length - 1
			});

			divTimeSliderLabel.text(formatDate(timestamps[timestamps.length - 1], layerInfo.getDateFormat()));

			var timestampInfo = {
				"timestamps" : timestamps
			};
			if (layerInfo.getDateFormat()) {
				timestampInfo["date-format"] = layerInfo.getDateFormat();
			}

			aTimestampsLayers[layerInfo.getId()] = timestampInfo;
		}

	});

	bus.listen("layer-timestamp-selected", function(e, layerId, d) {
		var timestampInfo = aTimestampsLayers[layerId];
		var position_i = -1;
		$.each(timestampInfo["timestamps"], function(position, date_value) {
			if (date_value.valueOf() == d.valueOf()) {
				position_i = position;
				$('#layer_time_slider_' + layerId).slider('value', position_i);

				var strDate = formatDate(d, timestampInfo["date-format"]);
				$('#layer_time_slider_label_' + layerId).text(strDate);
			}
		});
	});

	bus.listen("layer-visibility", function(e, layerId, visible) {
		var container = $("#layer_time_slider_container_" + layerId);
		if (visible) {
			container.show();
		} else {
			container.hide();
		}
	});
});