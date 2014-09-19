define([ "jquery", "layout" ], function($, layout) {
	var botonera = div = layout.layerTimeBoton; //$("<div/>").attr("style", "position:absolute;top:0; left:7em; z-index:2000");
	//botonera.appendTo(layout["map"]);

	return {
		newButton : function(text, callback) {
			var aButton = $("<a/>").html(text);
			aButton.addClass("blue_button").addClass("lang_button");
			aButton.appendTo(botonera);
			aButton.click(callback);
		},
		newText : function(callback) {
			var inputText = $("<input/>").attr("type", "text").attr("size", "15");
			inputText.attr("style", "margin-top:0.9em;margin-left:3em");
			inputText.appendTo(botonera);
			inputText.keypress(function(event) {
				if (event.which == 13) {
					callback(inputText.val());
				}
			});
		}
	};
});