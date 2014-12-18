define([ "jquery" ], function($) {

	var body = $("body");

	var divHeader = $("<div/>").attr("id", "header");
	body.append(divHeader);

	var divCenter = $("<div/>").attr("id", "center");
	var divMap = $("<div/>").attr("id", "map");
	divCenter.append(divMap);
	body.append(divCenter);

	// disable text selection on Explorer (done with CSS in other browsers)
	$(function() { document.body.onselectstart = function() { return false }})

	return {
		"header": divHeader,
		"map": divMap
	};
});
