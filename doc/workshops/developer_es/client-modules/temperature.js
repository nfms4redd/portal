define([ "message-bus", "layout", "olmap", "jquery", "jquery-ui", "toolbar" ], function(bus, layout, map, $) {
	bus.send("css-load", "modules/temperature.css");

	var getTemperature = function(e) {
		var lonlat = map.getLonLatFromPixel(e.xy);
		var requestData = {
			url : "http://api.openweathermap.org/data/2.5/weather?lat=" + //
			lonlat.lat + "&lon=" + lonlat.lon
		};
		bus.send("ajax", {
			dataType : "json",
			url : "proxy",
			data : $.param(requestData),
			success : function(data, textStatus, jqXHR) {
				window.alert("Temperatura en: " + data.name + //
				": " + (data.main.temp - 273.15));
			},
			errorMsg : "Cannot get temperature"
		});
	};

	var clickControl = new OpenLayers.Control();
	clickControl.handler = new OpenLayers.Handler.Click(clickControl, {
		'click' : getTemperature
	});

	var toolbar = $("#" + layout.toolbarId);
	$("<label/>").attr("for", "temperature-button").html("Temperatura").appendTo(toolbar);
	var btnTemp = $("<input/>").attr("type", "checkbox").appendTo(toolbar);
	btnTemp.attr("id", "temperature-button").attr("name", "temperature-button");
	btnTemp.addClass("blue_button lang_button");
	btnTemp.button().click(function() {
		if (btnTemp.is(':checked')) {
			bus.send("activate-exclusive-control", [ clickControl ]);
		} else {
			bus.send("activate-default-exclusive-control");
		}
	});

});