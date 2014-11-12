define([ "jquery" ], function($) {

	var body = $("body");

	var divHeader = $("<div/>").attr("id", "header");
	body.append(divHeader);

	var divCenter = $("<div/>").attr("id", "center");
	var divMap = $("<div/>").attr("id", "map");
	divCenter.append(divMap);
	body.append(divCenter);

	var divLayerList = $("<div/>").attr("id", "layers_container");
	body.append(divLayerList);

	var divActiveLayerList = $("<div/>").attr("id", "active_layers_container");
	body.append(divActiveLayerList);

	var divLayerListSelector = $("<div/>").attr("id", "layer_list_selector_pane");
	body.append(divLayerListSelector);

	// disable text selection on Explorer (done with CSS in other browsers)
	$(function() { document.body.onselectstart = function() { return false }})

	return {
		"header": divHeader,
		"map": divMap,
		"layers": divLayerList,
		"activeLayers": divActiveLayerList,
		"layerListSelector":  divLayerListSelector
	};
});
