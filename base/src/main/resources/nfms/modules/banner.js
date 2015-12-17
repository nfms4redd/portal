define([ "jquery", "layout", "i18n", "message-bus", "module" ], function($, layout, i18n, bus, module) {

	var config = module.config();

	if (!config["hide"]) {
		var divBanner = $("<div/>").attr("id", "banner");
		layout.header.prepend(divBanner);

		if (config["show-flag"]) {
			$("<div/>").attr("id", "flag").appendTo(divBanner);
		}
		if (config["show-logos"]) {
			$("<div/>").attr("id", "logos").appendTo(divBanner);
		}
		$("<span/>").attr("id", "title").html(i18n["title"]).appendTo(divBanner);
		$("<div/>").attr("id", "banner-izq").appendTo(divBanner);
	}
});
