define([ "jquery", "layout", "i18n" ], function($, layout, i18n) {
	$(document).trigger("css-load", "modules/banner.css");

	var divBanner = $("#" + layout.bannerId);

	var divFlag = $("<div/>").attr("id", "flag");
	var divLogos = $("<div/>").attr("id", "logos");
	var spnTitle = $("<span/>").attr("id", "title").html(i18n["title"]);
	divBanner.append(divFlag).append(divLogos).append(spnTitle);

	return divBanner;
});
