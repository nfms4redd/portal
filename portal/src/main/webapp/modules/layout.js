define([ "jquery" ], function($) {
    //var template = '<body><div id="header"><div id="banner"></div><div id="toolbar"><div id="time_slider_pane"></div></div></div><div id="layers_container"></div></body>'

	var body = $("body");

	var divHeader = $("<div/>").attr("id", "header");
	var divBanner = $("<div/>").attr("id", "banner");
	divHeader.append(divBanner);
	var divToolbar = $("<div/>").attr("id", "toolbar");
	var divTimeSlider = $("<div/>").attr("id", "time_slider_pane");
	divToolbar.append(divTimeSlider);
	divHeader.append(divToolbar);
	body.append(divHeader);

	var divCenter = $("<div/>").attr("id", "center");
	var divMap = $("<div/>").attr("id", "map");
	divCenter.append(divMap);
	body.append(divCenter);

	var divLayerList = $("<div/>").attr("id", "layers_container");
	body.append(divLayerList);

	return {
		"banner":     divBanner,
		"toolbar":    divToolbar,
		"timeSlider": divTimeSlider,
		"map":        divMap,
		"layers":     divLayerList
	};
});
