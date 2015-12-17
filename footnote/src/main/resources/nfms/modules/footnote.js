define(["module", "layout", "i18n", "jquery"], function(module, layout, i18n, $) {

	var config = module.config();
	var text = i18n[config.text] || config.text; // Required. Text can be a literal string or an i18n key reference.
	var link = i18n[config.link] || config.link; // Optional. Link can be a literal string or an i18n key reference.
	var align = config.align || "center";        // Optional. "left", "center" or "right". Defaults to "center".

	var container = $("<div/>").addClass("footnote").appendTo(layout.map);

	if (link) {
		$("<a />", {
			href: link,
			text: text,
			target: "_blank"
		}).css('text-align', align).appendTo(container);	
	} else {
		container.html(text).css('text-align', align);
	}

});
