define([ "jquery", "message-bus", "layout", "time-slider", "customization", "i18n" ], function($, bus, layout, timeSlider, customization, i18n) {
	bus.send("css-load", "modules/toolbar.css");

	var divToolbar = $("#" + layout.toolbarId);

	var divTimeSlider = $("<div/>").attr("id", "time_slider_pane");
	timeSlider("unique-slider", divTimeSlider);
	divToolbar.append(divTimeSlider);

	for (var i = 0; i < customization.languages.length; i++) {
		var language = customization.languages[i];
		var btnLanguage = $("<a href='?lang=" + language + "'/>").attr("id", "button_" + language).html(i18n[language]);
		btnLanguage.addClass("blue_button lang_button");
		if (customization.languageCode == language) {
			btnLanguage.addClass("selected");
		}
		divToolbar.append(btnLanguage);
	}

	bus.listen("add-layer", function(event, layerInfo) {
		if (layerInfo.hasOwnProperty("timestamps")) {
			var timestamps = layerInfo.timestamps;
			for (var i = 0; i < timestamps.length; i++) {
				bus.send("time-slider.add-timestamp.unique-slider", timestamps[i]);
			}
		}
	});

	return divToolbar;
});
