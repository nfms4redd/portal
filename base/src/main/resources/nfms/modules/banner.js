define([ "jquery", "layout", "i18n", "message-bus", "module" ], function($, layout, i18n, bus, module) {

	if (!module.config().hide) {
		var divBanner = $("<div/>").attr("id", "banner");
		layout.header.prepend(divBanner);

		var divFlag = $("<div/>").attr("id", "flag");
		var divLogos = $("<div/>").attr("id", "logos");
		var spnTitle = $("<span/>").attr("id", "title").html(i18n["title"]);
		divBanner.append(divFlag).append(divLogos).append(spnTitle);
	}
});
