define([ "jquery", "message-bus", "toolbar", "jquery-ui" ], function($, bus, toolbar, ui) {

	var timestampSet = {};
	var divTimeSlideContainer;
	
	divTimeSlideContainer = $("<div/>").attr("id", "time_slider_pane");
	divTimeSlideContainer.hide();
	toolbar.append(divTimeSlideContainer);
	

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.hasOwnProperty("timestamps")) {
			var layerTimestamps = layerInfo.timestamps;

			for (var i = 0; i < layerTimestamps.length; i++) {
				timestampSet[layerTimestamps[i]] = true;
			}
		}
	});

	bus.listen("layers-loaded", function() {
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

			// Send time-slider.selection message to show the date on the layer selection pane
			// right after page load
			divTimeSlider.slider("value", lastTimestampIndex);

			bus.listen("time-slider.selection", function(event, date){
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
			});
		}
	});
});
