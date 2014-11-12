define([ "jquery", "message-bus", "layout", "customization", "i18n", "mustache" ], function($, bus, layout, customization, i18n, mustache) {
	var view = {
		langs : customization.languages,
		selectedClass : function() {
			return this == customization.languageCode ? "selected" : "";
		}
	};

	var template = '{{#langs}}<a class="blue_button lang_button {{selectedClass}}" href="?lang={{code}}" id="button_{{code}}">{{name}}</a>{{/langs}}';
	var output = mustache.render(template, view);

	var divToolbar = $("<div/>").attr("id", "toolbar");
	divToolbar.append(output);
	layout.header.append(divToolbar);

	return divToolbar;
});
