define([ "jquery", "i18n", "message-bus" ], function($, i18n, bus) {
	var btnLegend = $("<a/>").appendTo("body");
	btnLegend.attr("id", "toggle_legend");
	btnLegend.addClass("blue_button");
	btnLegend.html(i18n["legend_button"]);
	btnLegend.click(function(){
		bus.send("toggle-legend");
	});
});