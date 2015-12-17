define([ "layout", "message-bus", "module", "jquery" ], function(layout, bus, module) {

	var priorities = module.config();

	var divToolbar = $("<div/>").attr("id", "toolbar");
	layout.header.append(divToolbar);

	bus.listen("modules-loaded", function() {
		var sortedChildren = divToolbar.children().sort(function(child1, child2) {
			var priority1 = priorities[$(child1).attr("id")];
			// If no priority, then zero
			priority1 = priority1 ? priority1 : 0;
			var priority2 = priorities[$(child2).attr("id")];
			// If no priority, then zero
			priority2 = priority2 ? priority2 : 0;
			if (priority1 > priority2) {
				return -1;
			} else if (priority1 < priority2) {
				return 1;
			} else {
				return 0;
			}
		});
		divToolbar.append(sortedChildren);
	});

	return divToolbar;
});
