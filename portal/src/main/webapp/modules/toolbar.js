define([ "jquery", "time-slider", "customization", "i18n" ], function($, timeSlider, customization, i18n) {
	$(document).trigger("css-load", "modules/toolbar.css");

	$(document).bind("init-toolbar", function(event, div) {
		var divToolbar = $("<div/>").attr("id", "toolbar");

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
		div.append(divToolbar);
	});

	$(document).bind("add-layer", function(event, layerInfo) {
		if (layerInfo.hasOwnProperty("timestamps")) {
			var timestamps = layerInfo.timestamps;
			for (var i = 0; i < timestamps.length; i++) {
				$(document).trigger("time-slider.add-timestamp.unique-slider", timestamps[i]);
			}
		}
	});
});
