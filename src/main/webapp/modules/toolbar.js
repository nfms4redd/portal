define([ "jquery", "message-bus", "layout", "customization", "i18n", "mustache" ], function($, bus, layout, customization, i18n, mustache) {
	var view = {
		langs: customization.languages,
		lang_code: function () { return this[0]; },
		lang_name: function () { return this[1]; },
		selectedClass : function() {
			return this == customization.languageCode ? "selected" : "";
		}
	};

	var template = '{{#langs}}<a class="blue_button lang_button {{selectedClass}}" href="?lang={{lang_code}}" id="button_{{lang_code}}">{{lang_name}}</a>{{/langs}}';
	var output = mustache.render(template, view);

	var divToolbar = layout.toolbar;
	divToolbar.append(output);

	return divToolbar;
});
