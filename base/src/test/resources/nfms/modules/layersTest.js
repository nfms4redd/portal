describe("board tests", function() {

	var injector;
	var bus;
	var customization;
	var module;

	beforeEach(function(done) {
		require.config({
			"baseUrl" : "src/",
			"paths" : {
				"message-bus" : "/messagebus/message-bus",
				"jquery" : "/jslib/jquery-2.1.0"
			},
			"config" : {
				"layers" : {
					"default-server" : "http://demo1.geo-solutions.it",
					"wmsLayers" : [ {
						"id" : "blue-marble",
						"baseUrl" : "http://rdc-snsf.org/diss_geoserver/wms",
						"wmsName" : "unredd:blue_marble",
						"imageFormat" : "image/jpeg"
					} ],
					"portalLayers" : [ {
						"id" : "blue-marble",
						"label" : "Blue marble",
						"layers" : [ "blue-marble" ]
					} ],
					"groups" : [ {
						"id" : "base",
						"label" : "Base",
						"items" : [ {
							"id" : "innerbase",
							"label" : "General purpose",
							"items" : [ "blue-marble" ]
						} ]
					} ]
				}
			}
		});
		require([ "/test-jslib/Squire.js" ], function(Squire) {
			if (injector != null) {
				injector.clean();
				injector.remove();
			}
			injector = new Squire();
			injector.require([ "message-bus" ], function(loadedModule) {
				bus = loadedModule;
				done();
			});

			injector.mock("customization", {
				languageCode : "fr"
			});
		});
	});

	it("layers init", function(done) {
		spyOn(bus, "listen");
		injector.require([ "layers" ], function() {
			expect(bus.listen).toHaveBeenCalled();
			done();
		});
	});

	it("layers process", function(done) {
		injector.require([ "layers" ], function() {
			bus.listen("layers-loaded", function(e, layerRoot) {
				done();
			});
			bus.send("modules-loaded");
		});
	});

	it("remove layer in second level", function(done) {
		injector.require([ "layers" ], function() {
			bus.listen("layers-loaded", function(e, layerRoot) {
				var layer = layerRoot.getPortalLayer("blue-marble");
				if (layer != null) { // to ignore reentering calls
					layer.remove();
					var group = layerRoot.getGroup("innerbase");
					expect(group.items.length).toBe(0);

					done();
				}
			});
			bus.send("modules-loaded");
		});
	});
});