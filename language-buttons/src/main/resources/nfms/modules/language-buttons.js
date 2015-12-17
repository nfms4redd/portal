define([ "toolbar", "customization", "mustache" ], function(toolbar, customization, mustache) {
	var view = {
		langs : customization.languages,
		selectedClass : function() {
			return this == customization.languageCode ? "selected" : "";
		}
	};

	var template = '{{#langs}}<a class="blue_button lang_button {{selectedClass}}" href="?lang={{code}}" id="button_{{code}}">{{name}}</a>{{/langs}}';
	var output = mustache.render(template, view);
	toolbar.append(output);
});
