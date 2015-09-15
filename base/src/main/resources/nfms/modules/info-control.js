define([ "map", "message-bus", "customization", "openlayers", "jquery" ], function(map, bus, customization) {

	// Associates wmsLayers with controls
	var layerIdControl = {};

	// Associates portalLayers with the selected timestamps
	var layerIdInfo = {};

	// List of controls
	var controls = [];

	var addBoundsAndHighlightGeom = function(feature) {
		var bounds = null;
		var highlightGeom = null;
		
		if (feature.geometry) {
			bounds = feature["geometry"].getBounds();
			highlightGeom = feature["geometry"];
		} else if (feature.attributes["bbox"]) {
			var bbox = feature.attributes["bbox"];
			bounds = new OpenLayers.Bounds();
			bounds.extend(new OpenLayers.LonLat(bbox[0], bbox[1]));
			bounds.extend(new OpenLayers.LonLat(bbox[2], bbox[3]));
			highlightGeom = bounds.toGeometry();
		}
		
		feature["bounds"] = bounds;
		feature["highlightGeom"] = highlightGeom;
	};
	
	bus.listen("add-layer", function(e, layerInfo) {
		var wmsLayers = layerInfo.wmsLayers;
		for (var i = 0; i < wmsLayers.length; i++) {
			var wmsLayer = wmsLayers[i];
			if (!wmsLayer.hasOwnProperty("queryType")) {
				continue;
			}

			layerIdInfo[layerInfo.id] = {};

			var aliases = null;
			if (wmsLayer.hasOwnProperty("queryFieldNames")) {
				aliases = [];
				var fieldNames = wmsLayer["queryFieldNames"];
				var fieldAliases = wmsLayer["queryFieldAliases"];
				for (var j = 0; j < fieldNames.length; j++) {
					var alias = {
						"name" : fieldNames[j],
						"alias" : fieldAliases[j]
					};
					aliases.push(alias);
				}
			}
			var queryUrl = null;
			if (wmsLayer.hasOwnProperty("queryUrl")) {
				queryUrl = wmsLayer["queryUrl"];
			} else {
				queryUrl = wmsLayer["baseUrl"];
			}

			var control = null;
			if (wmsLayer["queryType"] == "wfs") {

				queryUrl = queryUrl.trim();
				queryUrl = queryUrl.replace(/wms$/, "wfs");
				queryUrl = queryUrl + "?request=GetFeature&service=wfs&" + //
				"version=1.0.0&outputFormat=application/json&srsName=EPSG:900913&" + //
				"typeName=" + wmsLayer["wmsName"] + "&propertyName=" + wmsLayer["queryFieldNames"].join(",");

				control = new OpenLayers.Control();
				control.handler = new OpenLayers.Handler.Click(control, {
					'click' : function(e) {
						if (layerIdInfo.hasOwnProperty(layerInfo.id) && !layerIdInfo[layerInfo.id]["visibility"]) {
							return;
						}

						// bbox parameter
						var point = map.getLonLatFromPixel(e.xy);
						var bboxFilter = //
						"  <ogc:Intersects>" + //
						"    <ogc:PropertyName>" + wmsLayer["queryGeomFieldName"] + "</ogc:PropertyName>" + //
						"    <gml:Point xmlns:gml=\"http://www.opengis.net/gml\" srsName=\"EPSG:900913\">" + //
						"      <gml:coordinates decimal=\".\" cs=\",\" ts=\" \">" + point.lon + "," + point.lat + "</gml:coordinates>" + //
						"    </gml:Point>" + //
						"  </ogc:Intersects>" //
						// " <ogc:BBOX>" + //
						// " <gml:Box xmlns:gml=\"http://www.opengis.net/gml\"
						// srsName=\"EPSG:900913\">" + //
						// " <gml:coordinates decimal=\".\" cs=\",\" ts=\" \">"
						// + (point.lon - 1) + "," + (point.lat - 1) + " " +
						// (point.lon + 1) + "," + (point.lat + 1) +
						// "</gml:coordinates>" + //
						// " </gml:Box>" + //
						// " </ogc:BBOX>" //
						;

						// time parameter
						var getFeatureMessage = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">";
						if (layerIdInfo.hasOwnProperty(layerInfo.id) && layerIdInfo[layerInfo.id].hasOwnProperty("timestamp")) {
							getFeatureMessage += //
							"  <ogc:And>" + //
							"" + bboxFilter + //
							"    <ogc:PropertyIsEqualTo>" + //
							"      <ogc:PropertyName>" + wmsLayer["queryTimeFieldName"] + "</ogc:PropertyName>" + //
							"      <ogc:Function name=\"dateParse\">" + //
							"        <ogc:Literal>yyyy-MM-dd</ogc:Literal>" + //
							"        <ogc:Literal>" + layerIdInfo[layerInfo.id]["timestamp"].toISO8601String() + "</ogc:Literal>" + //
							"      </ogc:Function>" + //
							"    </ogc:PropertyIsEqualTo>" + //
							"  </ogc:And>";
						} else {
							getFeatureMessage += bboxFilter;
						}
						getFeatureMessage += "</ogc:Filter>";
						var url = queryUrl + "&FILTER=" + encodeURIComponent(getFeatureMessage);
						bus.send("clear-info-features");

						bus.send("ajax", {
							dataType : "json",
							url : "proxy",
							data : $.param({
								url : url
							}),
							success : function(data, textStatus, jqXHR) {
								var features = new OpenLayers.Format.GeoJSON().read(data);
								if (features.length > 0) {
									$.each(features, function(index, feature) {
										feature["aliases"] = aliases;
										addBoundsAndHighlightGeom(feature);
									});
	
									bus.send("info-features", [ wmsLayer.id, features, e.xy.x, e.xy.y ]);
								}
							},
							errorMsg : "Cannot get info for layer " + layerInfo.label
						});

					}
				});

			} else {
				var control = new OpenLayers.Control.WMSGetFeatureInfo({
					url : queryUrl,
					layerUrls : [ wmsLayer["baseUrl"] ],
					title : 'Identify features by clicking',
					infoFormat : 'application/vnd.ogc.gml',
					drillDown : false,
					queryVisible : true,
					maxFeatures : 5,
					handlerOptions : {
						"click" : {
							'single' : true,
							'double' : false
						}
					},
					eventListeners : {
						getfeatureinfo : function(evt) {
							if (evt.features && evt.features.length > 0) {
								var features = evt.features;
								var featureAliases = null;
								if (aliases != null) {
									featureAliases = aliases;
								} else if (features.length > 0) {
									var attributes = features[0].attributes;
									featureAliases = [];
									for (attributeName in attributes) {
										featureAliases.push({
											"name" : attributeName,
											"alias" : attributeName
										});
									}
								}

								// re-project to Google projection and add
								// aliases
								epsg4326 = new OpenLayers.Projection("EPSG:4326");
								epsg900913 = new OpenLayers.Projection("EPSG:900913");
								$.each(evt.features, function(index, feature) {
									feature["aliases"] = featureAliases;
									if (feature.geometry) {
										if (wmsLayer.hasOwnProperty("queryHighlightBounds") && wmsLayer["queryHighlightBounds"]) {
											feature.geometry = feature.geometry.getBounds().toGeometry();
										}
										feature.geometry.transform(epsg4326, epsg900913);
									}
									addBoundsAndHighlightGeom(feature);
								});

								bus.send("info-features", [ wmsLayer.id, evt.features, evt.xy.x, evt.xy.y ]);
							}
						},
						beforegetfeatureinfo : function() {
							var id = wmsLayer["id"];
							if (layerIdInfo.hasOwnProperty(layerInfo.id) && layerIdInfo[layerInfo.id].hasOwnProperty("timestamp")) {
								control.vendorParams = {
									"time" : layerIdInfo[layerInfo.id]["timestamp"].toISO8601String()
								};
							}

							bus.send("clear-info-features");
						}
					},
					formatOptions : {
						typeName : 'XXX',
						featureNS : 'http://www.openplans.org/unredd'
					}
				});
			}

			if (control != null) {
				layerIdInfo[layerInfo.id]["control"] = control;
				layerIdControl[wmsLayer["id"]] = control;
				controls.push(control);
			}
		}
	});

	bus.listen("layer-visibility", function(e, layerId, visibility) {
		/*
		 * Necessary for the WFS info
		 */
		if (layerIdInfo.hasOwnProperty(layerId)) {
			layerIdInfo[layerId] = $.extend(layerIdInfo[layerId], {
				"visibility" : visibility
			});
		}
	});

	bus.listen("layer-timestamp-selected", function(e, layerId, timestamp) {
		if (layerIdInfo.hasOwnProperty(layerId)) {
			layerIdInfo[layerId] = $.extend(layerIdInfo[layerId], {
				"timestamp" : timestamp
			});
		}
	});

	bus.listen("layers-loaded", function() {
		bus.send("set-default-exclusive-control", [ controls ]);
		bus.send("activate-default-exclusive-control");

		// Set the OL layers for each control (only effective with the WMS
		// control)
		for ( var wmsLayerId in layerIdControl) {
			var control = layerIdControl[wmsLayerId];
			var layer = map.getLayersByName(wmsLayerId)[0];
			control.layers = new Array();
			control.layers.push(layer);
		}
	});
});
