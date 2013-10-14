/*
 * Requirejs does not load css. Either we put it here, either in the
 * HTML. See http://requirejs.org/docs/faq-advanced.html#css
 * 
 * This is more modular, but the CSS may take time to load.
 */
define([ "message-bus" ], function(bus) {
	bus.subscribe("css-load", function(event, cssURL) {
		var link = document.createElement("link");
		link.type = "text/css";
		link.rel = "stylesheet";
		link.href = cssURL;
		document.getElementsByTagName("head")[0].appendChild(link);
	});

});
