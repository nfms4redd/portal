define([ "jquery", "message-bus", "toolbar", "jquery-ui" ], function($, bus, toolbar, ui) {

	var timestampSet = {};
	var divTimeSlideContainer;

	divTimeSlideContainer = $("<div/>").attr("id", "time_slider_pane");
	divTimeSlideContainer.hide();
	toolbar.append(divTimeSlideContainer);

	function timeSliderSelection(event, date) {
		var divTimeSlider = $("#time_slider");
		var position = divTimeSlider.slider("value");
		var d = new Date();
		d.setISO8601(timestamps[position]);
		if (d.getTime() != date.getTime()) {
			for (var i = 0; i < timestamps.length; i++) {
				d.setISO8601(timestamps[i]);
				if (d.getTime() == date.getTime()) {
					divTimeSlider.slider("value", i);
					divTimeSliderLabel.text(date.getLocalizedDate());
					break;
				}
			}
		}
	}

	var draw = function() {
		var timestamps, div, lastTimestampIndex;

		timestamps = $.map(timestampSet, function(value, key) {
			return key;
		}).sort();
		lastTimestampIndex = timestamps.length - 1;

		if (timestamps.length > 0) {
			div = divTimeSlideContainer;

			var divTimeSliderLabel = $('<span id="time_slider_label"/>');
			div.append(divTimeSliderLabel);

			var divTimeSlider = $('<div id="time_slider"/>');
			div.append(divTimeSlider);

			divTimeSlider.slider({
				change : function(event, ui) {
					var d = new Date();
					d.setISO8601(timestamps[ui.value]);
					bus.send("time-slider.selection", d);
				},
				slide : function(event, ui) {
					divTimeSliderLabel.text(Date.getLocalizedDate(timestamps[ui.value]));
				},
				max : lastTimestampIndex,
				value : lastTimestampIndex
			});

			divTimeSliderLabel.text(Date.getLocalizedDate(timestamps[lastTimestampIndex]));

			div.show();

			// Send time-slider.selection message to show the date on the layer
			// selection pane
			// right after page load
			divTimeSlider.slider("value", lastTimestampIndex);

			bus.listen("time-slider.selection", timeSliderSelection);
		}
	};

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.getTimestamps() != null) {
			var layerTimestamps = layerInfo.getTimestamps();

			for (var i = 0; i < layerTimestamps.length; i++) {
				timestampSet[layerTimestamps[i]] = true;
			}
		}
	});

	bus.listen("layers-loaded", draw);

	bus.listen("reset-layers", function() {
		timestampSet = {};
		divTimeSlideContainer.hide();
		$("#time_slider_label").remove();
		$("#time_slider").remove();
		bus.stopListen("time-slider.selection", timeSliderSelection);
	});

});
