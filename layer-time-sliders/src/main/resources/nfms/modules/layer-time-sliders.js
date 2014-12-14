define([ "jquery", "message-bus", "layout", "botonera", "map", "jquery-ui" ], function($, bus, layout, botonera, map) {
    var aLayers=[];
    var aTimestampsLayers={};
	var divTimeSliders = $("<div/>").attr("id", "layerTimeSliders");

	botonera.newButton("temporal", function() {
		divTimeSliders.dialog({
			closeOnEscape : true,
			width : "80%",
			resizable : true
		});
	});

	bus.listen("add-layer", function(event, layerInfo) {
		var timestamps = [];
		$.each(layerInfo.wmsLayers, function(index, wmsLayer) {
			if (wmsLayer.hasOwnProperty("timestamps")) {
				for (var i = 0; i < wmsLayer.timestamps.length; i++) {
					var d = new Date();
					d.setISO8601(wmsLayer.timestamps[i]);
					timestamps.push(d);
				}
			}
		});

		if (timestamps.length > 0) {
			timestamps.sort(function(a, b) {
				return a - b;
			});

			$("<div/>").html(layerInfo.label).appendTo(divTimeSliders);
			var divTimeSlider = $("<div id='layer_time_slider_" + layerInfo.id + "' class='layers_time_slider' />").appendTo(divTimeSliders);

			var divTimeSliderLabel = $("<div id='layer_time_slider_label_" + layerInfo.id + "'/>").appendTo(divTimeSliders);

			divTimeSlider.slider({
				change : function(event, ui) {
					if (event.originalEvent) {
						var date = timestamps[ui.value];
						$.each(layerInfo.wmsLayers, function(index, wmsLayer) {
							var layer = map.getLayer(wmsLayer.id);
							layer.mergeNewParams({
								'time' : date.toISO8601String()
							});
							bus.send("layer-time-slider.selection", [layerInfo.id,date]);
						});
					}else{ //Programatic change
						//alert('programatic');
					};
					
				},
				slide : function(event, ui) {
					var date = timestamps[ui.value];
					divTimeSliderLabel.text(date);
				},
				max : timestamps.length - 1,
				value : timestamps.length - 1
			});

			divTimeSliderLabel.text(timestamps[timestamps.length - 1]);
		
		   aTimestampsLayers[layerInfo.id]=timestamps;
		}

	});
	
	bus.listen("time-slider.selection",function(obj,d){

		console.log(aTimestampsLayers);
		$.each(aTimestampsLayers, function(layerid,steps){
			var position_i=-1,position_min=-1,position_max=-1;
			console.log(layerid);
			$.each(steps,function(position,date_value){
				if (date_value.valueOf()==d.valueOf()) {
					position_i=position;
					
				} else if (date_value.valueOf()<d.valueOf()) {
					position_min=position;
					//console.log(date_value+' menor');
				}else{
					if (position_max==-1) {position_max=position;};
					//console.log(date_value+' mayor');
				};
				});
			console.log(layerid+' -> '+position_i+', '+position_min+', '+position_max);
			var pos;
			if (position_i>-1)
				{pos=position_i;}
			else{ pos=position_min;}
			$('#layer_time_slider_'+layerid).slider('value',pos);
		}
		);
	});
	bus.listen("layer-visibility", function (portalLayerid,visibility){
/*		if (visibility==false) {text='ocultar'}
		else {text='mostrar'};
		alert(portalLayerid+' '+text+visibility);
	*/	
	});
});