describe("board tests", function() {

	var injector;
	var bus;
	var customization;
	var module;

	beforeEach(function(done) {
		require.config({
			"baseUrl" : "src/",
			"paths" : {
				"jquery" : "/jslib/jquery-2.1.0"
			},
			"config" : {
				"layers" : {
					"default-server" : "http://demo1.geo-solutions.it",
					"wmsLayers" : [],
					"portalLayers" : [],
					"groups" : []
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
});