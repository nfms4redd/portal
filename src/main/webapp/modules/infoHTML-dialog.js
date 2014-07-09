define([ "jquery", "message-bus", "map" ], function($, bus, map) {

	bus.listen("infoHTML-text",function(evt, name, html, x, y, features){
		infoPopup = $("#info_popup");
		if (infoPopup.length === 0) {
			infoPopup = $("<div/>").attr("id", "info_popup");
		} else {
			infoPopup.empty();
		}
		infoPopup.dialog({
			closeOnEscape : true,
			maxHeight: 550,
			width : 350,
			resizable : false,
			close : function(event, ui) {
				bus.send("clear-highlighted-features");
				map.getLayer("Highlighted Features").destroyFeatures();
			},
			autoOpen : false
		});
		
		div = $("<div id=\"allPopupContent\"></div>");
		infoPopup.append(div);

		infoPopup.mouseenter(function() { bus.send("highlight-feature", [features]); });
		infoPopup.mouseleave(function()  { bus.send("clear-highlighted-features"); });
		
		zoomto = $("<td class=\"td_right\"/>");
		zoomto.append("<a style=\"color:white\" class=\"feature_link\" href=\"#\" id=\"zoom_to_feature\">Zoom to area</a>");
		zoomto.click(function() {
			for(var i in features){
				//compute the bbox of all features
			}
			bus.send("zoom-to", features[0].geometry.getBounds().scale(1.2));
		});
		
		div.append(html);
		infoPopup.append(zoomto);
//		if(infoPopup.outerHeight() > 550){;
//			infoPopup.dialog({height : 550});
//		}
		infoPopup.dialog('open');
		infoPopup.dialog('moveToTop');
		
	});

	bus.listen("highlight-feature", function(event, features) {
		var highlightLayer = map.getLayer("Highlighted Features");
		highlightLayer.removeAllFeatures();
		highlightLayer.addFeatures(features);
		highlightLayer.redraw();
	});

	bus.listen("clear-highlighted-features", function() {
		var highlightLayer = map.getLayer("Highlighted Features");
		highlightLayer.removeAllFeatures();
		highlightLayer.redraw();
	});

});