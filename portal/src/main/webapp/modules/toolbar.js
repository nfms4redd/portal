define([ "jquery", "message-bus", "layout", "customization", "i18n", "mustache" ], function($, bus, layout, customization, i18n, mustache) {
	view = {
		lang_codes: customization.languages,
		lang_name: function () { return i18n[this] }
	};

	var template = '{{#lang_codes}}<a class="blue_button lang_button" href="?lang={{.}}" id="button_{{.}}">{{lang_name}}</a>{{/lang_codes}}'
	var output = mustache.render(template, view)

	var divToolbar = layout.toolbar;
	divToolbar.append(output)

	/*
	for (var i = 0; i < customization.languages.length; i++) {
		var language = customization.languages[i];
		var btnLanguage = $("<a href='?lang=" + language + "'/>").attr("id", "button_" + language).html(i18n[language]);
		btnLanguage.addClass("blue_button lang_button");
		if (customization.languageCode == language) {
			btnLanguage.addClass("selected");
		}
		divToolbar.append(btnLanguage);
	}
	*/

	return divToolbar;
});
