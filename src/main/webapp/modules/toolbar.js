define([ "jquery", "message-bus", "layout", "customization", "i18n", "mustache" ], function($, bus, layout, customization, i18n, mustache) {
	var view = {
		lang_codes: customization.languages,
		lang_name: function () { return i18n[this]; },
		selectedClass : function() {
			return this == customization.languageCode ? "selected" : "";
		}
	};

	var template = '{{#lang_codes}}<a class="blue_button lang_button {{selectedClass}}" href="?lang={{.}}" id="button_{{.}}">{{lang_name}}</a>{{/lang_codes}}';
	var output = mustache.render(template, view);

	var divToolbar = layout.toolbar;
	divToolbar.append(output);

	return divToolbar;
});
