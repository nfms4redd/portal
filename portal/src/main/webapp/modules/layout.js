define([ "jquery" ], function($) {
	var body = $("body");

	var divHeader = $("<div/>").attr("id", "header");
	var divBanner = $("<div/>").attr("id", "banner");
	divHeader.append(divBanner);
	var divToolbar = $("<div/>").attr("id", "toolbar");
	divHeader.append(divToolbar);
	body.append(divHeader);

	var divCenter = $("<div/>").attr("id", "center");
	var divMap = $("<div/>").attr("id", "map");
	divCenter.append(divMap);
	body.append(divCenter);

	var divLayerList = $("<div/>").attr("id", "layers_container");
	body.append(divLayerList);

	return {
		"bannerId" : divBanner.attr("id"),
		"toolbarId" : divToolbar.attr("id"),
		"mapId" : divMap.attr("id"),
		"layersId" : divLayerList.attr("id")
	};
});