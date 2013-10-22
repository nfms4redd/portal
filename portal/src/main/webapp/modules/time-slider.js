define([ "jquery", "message-bus", "layout", "jquery-ui", "i18n" ], function($, bus, layout, ui, i18n) {
	var timestamps = [];

	var getLocalizedDate = function(date) {
		var defaultMonths = [ "Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July", "Aug.", "Sep.", "Oct.", "Nov.", "Dec." ];
		var months = i18n.months ? eval(i18n.months) : defaultMonths;
		var arr = date.split("-");
		if (arr[1]) {
			arr[1] = months[arr[1] - 1];
		}
		return arr.reverse().join(" ");
	};

	var div = $("#" + layout.timeSliderId);
	var divTimeSlider = $("<div/>").attr("id", "time_slider");
	div.append(divTimeSlider);
	divTimeSlider.slider({
		change : function(event, ui) {
			var d = new Date();
			d.setISO8601(timestamps[ui.value]);
			divTimeSliderLabel.text(getLocalizedDate(timestamps[ui.value]));
			bus.send("time-slider.selection", d);
		}
	});
	divTimeSlider.slider("option", "min", 0);
	divTimeSlider.slider("option", "max", 0);

	var divTimeSliderLabel = $("<div/>").attr("id", "time_slider_label");
	div.append(divTimeSliderLabel);

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.hasOwnProperty("timestamps")) {
			var layerTimestamps = layerInfo.timestamps;
			for (var i = 0; i < layerTimestamps.length; i++) {
				var layerTimestamp = layerTimestamps[i];
				var exists = false;
				for ( var i = 0; i < timestamps.length; i++) {
					if (timestamps[i] === layerTimestamp) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					timestamps.push(layerTimestamp);
					timestamps.sort();
				}

				divTimeSlider.slider("option", "max", timestamps.length - 1);
				divTimeSlider.slider("value", timestamps.length - 1);
			}
		}
	});

});
