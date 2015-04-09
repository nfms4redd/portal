define([ "module", "toolbar", "message-bus", "jquery", "tipsy" ], function(module, toolbar, bus, $) {

	var steps;

	var showStep = function(stepIndex) {
		var step = steps[stepIndex];
		$("#" + step.id).tipsy("show");
		if (!step.listener) {
			$("#tour-next-" + stepIndex).click(function() {
				$("#" + step.id).tipsy("hide");
				console.log(step.next);
				for (event in step.next) {
					console.log(event);
					bus.send(event, step.next[event]);
				}
				showStep(stepIndex + 1);
			});
			step.listener = true;
		}
	};

	bus.listen("modules-loaded", function() {
		steps = module.config().steps;
		for (var i = 0; i < steps.length; i++) {
			var step = steps[i];
			var text = step.text + "<br/><br/><button id='tour-next-" + i + "'>Next</button>";
			$("#" + step.id).attr("tour-info", text).tipsy({
				gravity : step.gravity,
				trigger : "manual",
				title : "tour-info",
				html : true
			});
			step["listener"] = false;
		}
	});

	var btn = $("<a/>").attr("id", "tour-button").addClass("blue_button").html("Guía interactiva");
	btn.appendTo(toolbar);
	btn.click(function() {
		showStep(0);
		return false;
	});

});