define([ "module", "toolbar", "message-bus", "jquery", "tipsy" ], function(module, toolbar, bus, $) {

	var steps = module.config().steps;

	var showStep = function(stepIndex) {
		var step = steps[stepIndex];

		var text = step.text + "<br/><br/><button id='tour-next-" + stepIndex + "'>Seguir</button>";
		var tipsyConf = {
			trigger : "manual",
			title : "tour-info",
			html : true,
			opacity : 1
		};
		if (step["gravity"]) {
			tipsyConf["gravity"] = step.gravity;
		} else {
			tipsyConf["gravity"] = $.fn.tipsy.autoNS;
		}
		$("#" + step.id).attr("tour-info", text).tipsy(tipsyConf);

		$("#" + step.id).tipsy("show");
		$("#tour-next-" + stepIndex).click(function() {
			$("#" + step.id).tipsy("hide");
			for (event in step.next) {
				var times = 1;
				if (!isNaN(parseInt(event.charAt(0)))) {
					times = parseInt(event.charAt(0));
					event = event.substr(1);
				}
				for (var i = 0; i < times; i++) {
					var parameters = step.next[event];
					for (paramIndex in parameters) {
						var parameter = parameters[paramIndex];
						if (typeof parameter == "string" && parameter.charAt(0) == "X") {
							parameters[paramIndex] = eval(parameter.substr(1));
						}
					}
					bus.send(event, parameters);
				}
			}
			showStep(stepIndex + 1);
		});
	};

	var btn = $("<a/>").attr("id", "tour-button").addClass("blue_button").html("Guía interactiva");
	btn.appendTo(toolbar);
	btn.click(function() {
		showStep(0);
		return false;
	});

});