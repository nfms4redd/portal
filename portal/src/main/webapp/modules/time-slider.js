define([ "jquery", "jquery-ui", "i18n" ], function($, ui, i18n) {
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

	return function(id, div) {
		var divTimeSlider = $("<div/>").attr("id", "time_slider_" + id);
		div.append(divTimeSlider);
		divTimeSlider.slider({
			change : function(event, ui) {
				var d = new Date();
				d.setISO8601(timestamps[ui.value]);
				divTimeSliderLabel.text(getLocalizedDate(timestamps[ui.value]));
				$(document).trigger("time-slider.selection." + id, d);
			}
		});
		divTimeSlider.slider("option", "min", 0);
		divTimeSlider.slider("option", "max", 0);

		var divTimeSliderLabel = $("<div/>").attr("id", "time_slider_label_" + id);
		div.append(divTimeSliderLabel);

		$(document).bind("time-slider.add-timestamp." + id, function(event, timestamp) {
			var exists = false;
			for (var i = 0; i < timestamps.length; i++) {
				if (timestamps[i] === timestamp) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				timestamps.push(timestamp);
				timestamps.sort();
			}

			divTimeSlider.slider("option", "max", timestamps.length - 1);
			divTimeSlider.slider("value", timestamps.length - 1);
		});
	};
});
