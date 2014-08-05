define([ "jquery", "layout", "i18n", "message-bus" ], function($, layout, i18n, bus) {

	var divFlag = $("<div/>").attr("id", "flag");
	var divLogos = $("<div/>").attr("id", "logos");
	var spnTitle = $("<span/>").attr("id", "title").html(i18n["title"]);
	layout.banner.append(divFlag).append(divLogos).append(spnTitle);

	return layout.banner;
});
