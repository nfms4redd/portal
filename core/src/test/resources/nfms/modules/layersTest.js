describe("board tests", function() {

	var bus;
	var customization;
	var module;

	beforeEach(function(done) {
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

		require.config({
			"baseUrl" : "src/",
			"paths" : {
				"jquery" : "/jslib/jquery-2.1.0"
			}
		});
		require([ "message-bus" ], function(loadedModule) {
			bus = loadedModule;
			done();
		});
	});

	it("layers init", function(done) {
		spyOn(bus, "listen");
		require(["layers"], function(){
			expect(bus.listen).toHaveBeenCalled();
			done();
		});
	});
});