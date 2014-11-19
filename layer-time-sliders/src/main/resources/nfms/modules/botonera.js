define([ "jquery", "toolbar" ], function($, toolbar) {

	return {
		newButton : function(text, callback) {
			var aButton = $("<a/>").html(text);
			aButton.addClass("blue_button").addClass("lang_button");
			aButton.appendTo(toolbar);
			aButton.click(callback);
		},
		newText : function(callback) {
			var inputText = $("<input/>").attr("type", "text").attr("size", "15");
			inputText.attr("style", "margin-top:0.9em;margin-left:3em");
			inputText.appendTo(toolbar);
			inputText.keypress(function(event) {
				if (event.which == 13) {
					callback(inputText.val());
				}
			});
		}
	};
});