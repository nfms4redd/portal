define([ "jquery", "message-bus", "layout", "jquery-ui", "i18n" ], function($, bus, layout, ui, i18n) {

	var timestampSet = {};

	var getLocalizedDate = function(date) {
		var defaultMonths = [ "Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July", "Aug.", "Sep.", "Oct.", "Nov.", "Dec." ];
		var months = i18n.months ? eval(i18n.months) : defaultMonths;
		var arr = date.split("-");

		if (arr[1]) {
			arr[1] = months[arr[1] - 1];
		}

		return arr[1] + " " + arr[0];
	};

	bus.listen("add-layer", function(event, layerInfo) {
		$.each(layerInfo.wmsLayers, function(index, wmsLayer) {
			if (wmsLayer.hasOwnProperty("timestamps")) {
				var layerTimestamps = wmsLayer.timestamps;

				for (var i = 0; i < layerTimestamps.length; i++) {
					timestampSet[layerTimestamps[i]] = true;
				}
			}
		});
	});

	bus.listen("layers-loaded", function() {
		var timestamps, div, divTimeSlider, divTimeSliderLabel, lastTimestampIndex;

		timestamps = $.map(timestampSet, function(value, key) {
			return key;
		}).sort();
		lastTimestampIndex = timestamps.length - 1;

		if (timestamps.length > 0) {
			div = layout.timeSlider;

			divTimeSlider = $('<div id="time_slider"/>');
			div.append(divTimeSlider);

			divTimeSliderLabel = $('<div id="time_slider_label"/>');
			div.append(divTimeSliderLabel);

			divTimeSlider.slider({
				change : function(event, ui) {
					var d = new Date();
					d.setISO8601(timestamps[ui.value]);
					bus.send("time-slider.selection", d);
				},
				slide : function(event, ui) {
					divTimeSliderLabel.text(getLocalizedDate(timestamps[ui.value]));
				},
				max : lastTimestampIndex,
				value : lastTimestampIndex
			});

			divTimeSliderLabel.text(getLocalizedDate(timestamps[lastTimestampIndex]));

			div.show();
		}
	});
});
