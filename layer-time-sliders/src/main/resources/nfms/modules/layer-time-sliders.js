define([ "jquery", "message-bus", "layout", "map", "layer-list-selector", "moment", "jquery-ui" ], function($, bus, layout, map, layerListSelector, moment) {
    var aTimestampsLayers={};
	var divTimeSliders = $("<div/>").attr("id", "layerTimeSliders").addClass("layer_container_panel");
	layerListSelector.registerLayerPanel("layer_slider_selector", "Temporal", divTimeSliders);

	var formatDate = function(date, dateFormat) {
		var format;
		if (dateFormat) {
			format = dateFormat;
		} else {
			format = "MMM-YYYY";
		}
		return moment(date).format(format);
	}
	
	bus.listen("add-layer", function(event, layerInfo) {
		var timestamps = [];
		if (layerInfo.hasOwnProperty("timestamps")) {
			for (var i = 0; i < layerInfo.timestamps.length; i++) {
				var d = new Date();
				d.setISO8601(layerInfo.timestamps[i]);
				timestamps.push(d);
			}
		}

		if (timestamps.length > 0) {
			timestamps.sort(function(a, b) {
				return a - b;
			});

			$("<div/>").html(layerInfo.label).addClass("layer-time-slider-title").appendTo(divTimeSliders);
			var divTimeSliderLabel = $("<span id='layer_time_slider_label_" + layerInfo.id + "'/>").appendTo(divTimeSliders);
			var divTimeSlider = $("<div id='layer_time_slider_" + layerInfo.id + "' class='layers_time_slider' />").appendTo(divTimeSliders);
			divTimeSlider.addClass("layer-time-slider");

			divTimeSlider.slider({
				change : function(event, ui) {
					if (event.originalEvent) {
						var date = timestamps[ui.value];
						$.each(layerInfo.wmsLayers, function(index, wmsLayer) {
							var layer = map.getLayer(wmsLayer.id);
							layer.mergeNewParams({
								'time' : date.toISO8601String()
							});
							bus.send("layer-time-slider.selection", [layerInfo.id,date]);
						});
					}else{ //Programatic change
						//alert('programatic');
					};
					
				},
				slide : function(event, ui) {
					var date = timestamps[ui.value];
					divTimeSliderLabel.text(formatDate(date, layerInfo["date-format"]));
				},
				max : timestamps.length - 1,
				value : timestamps.length - 1
			});

			divTimeSliderLabel.text(formatDate(timestamps[timestamps.length - 1], layerInfo["date-format"]));
		
   		    var timestampInfo = {
				"timestamps" : timestamps
			};
			if (layerInfo["date-format"]) {
				timestampInfo["date-format"] = layerInfo["date-format"];
			}

			aTimestampsLayers[layerInfo.id] = timestampInfo;
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
});