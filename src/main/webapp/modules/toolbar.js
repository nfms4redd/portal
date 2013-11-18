define([ "jquery", "message-bus", "layout", "customization", "i18n" ], function($, bus, layout, customization, i18n) {

	var divToolbar = layout.toolbar;

	for (var i = 0; i < customization.languages.length; i++) {
		var language = customization.languages[i];
		var btnLanguage = $("<a href='?lang=" + language + "'/>").attr("id", "button_" + language).html(i18n[language]);
		btnLanguage.addClass("blue_button lang_button");
		if (customization.languageCode == language) {
			btnLanguage.addClass("selected");
		}
		divToolbar.append(btnLanguage);
	}
	
	return divToolbar;
});
