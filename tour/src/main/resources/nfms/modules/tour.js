define([ "module", "toolbar", "message-bus", "jquery", "tipsy" ], function(module, toolbar, bus, $) {

	var steps = module.config().steps;

	var infoFeatures;

	var showStep = function(stepIndex) {
		var step = steps[stepIndex];

		var text = step.text + "<br/><br/><button id='tour-next-" + stepIndex + "' style='margin-right:10px'>Seguir</button><button id='tour-close-" + stepIndex + "' style='margin-left:10px'>Cerrar</button>";
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
		var btnNext = $("#tour-next-" + stepIndex);
		btnNext.focus();
		btnNext.click(function() {
			$("#" + step.id).tipsy("hide");
			for (nextEvent in step.next) {
				var times = 1;
				if (!isNaN(parseInt(nextEvent.charAt(0)))) {
					times = parseInt(nextEvent.charAt(0));
					nextEvent = nextEvent.substr(1);
				}
				for (var i = 0; i < times; i++) {
					var parameters = step.next[nextEvent];
					for (paramIndex in parameters) {
						var parameter = parameters[paramIndex];
						if (typeof parameter == "string" && parameter.charAt(0) == "X") {
							parameters[paramIndex] = eval(parameter.substr(1));
						}
					}
					bus.send(nextEvent, parameters);
				}
			}
			
			if (step.wait) {
				var fnc = function() {
					showStep(stepIndex + 1);
					bus.stopListen(step.wait, fnc);
				};
				bus.listen(step.wait, fnc);
			} else {
				showStep(stepIndex + 1);
			}
			
		});
		$("#tour-close-" + stepIndex).click(function() {
			$("#" + step.id).tipsy("hide");
		});
	};

	var btn = $("<a/>").attr("id", "tour-button").addClass("blue_button").html("Guía interactiva");
	btn.appendTo(toolbar);
	btn.click(function() {
		showStep(0);
		return false;
	});

	/*
	 * helpers to highlight and zoom info features
	 */
	bus.listen("info-features", function(event, wmsLayerId, features, x, y) {
		infoFeatures = features;
	});
	bus.listen("highlight-info-feature", function(event, index) {
		bus.send("highlight-feature", infoFeatures[index]["highlightGeom"]);
	});
	bus.listen("zoom-info-feature", function(event, index) {
		bus.send("zoom-to", infoFeatures[index]["bounds"].scale(1.2));
	});

});