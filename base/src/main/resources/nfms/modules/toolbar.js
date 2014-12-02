define([ "layout", "jquery" ], function(layout) {

	var divToolbar = $("<div/>").attr("id", "toolbar");
	layout.header.append(divToolbar);

	return divToolbar;
});
