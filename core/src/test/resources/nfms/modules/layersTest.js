describe("board tests", function() {

	var events;
	var bus;
	var customization;
	var module;

	beforeEach(function() {
		customization = {
			"languageCode" : "es"
		};
		module = {
			"config" : function() {
				var layersJson = {
					"default-server" : "http://demo1.geo-solutions.it",
					"wmsLayers" : [],
					"portalLayers" : [],
					"groups" : []
				};
				return layersJson;
			}
		};

		events = [];
		bus = {
			"send" : function(eventName, args) {
				events[eventName] = args;
			},
			"listen" : function() {
			}
		};
	});

	it("layers init", function() {
		spyOn(bus, "listen");
		_initModule("layers", [ $, bus, customization, module ]);
		expect(bus.listen).toHaveBeenCalled();
	});
});