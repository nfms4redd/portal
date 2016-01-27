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

	bus.listen("reset-layers", function() {
		layerIdControl = {};
		layerIdInfo = {};
		bus.send("set-default-exclusive-control", []);
		bus.send("activate-default-exclusive-control");
		for ( var c in controls) {
			var control = controls[c];
			control.destroy();
		}
		controls = [];
	});

	bus.listen("add-layer", function(e, layerInfo) {
		var mapLayers = layerInfo.getMapLayers();
		for (var i = 0; i < mapLayers.length; i++) {
			var mapLayer = mapLayers[i];
			if (!mapLayer.isQueryable()) {
				continue;
			}

			layerIdInfo[layerInfo.getId()] = {};

			var aliases = null;
			if (mapLayer.hasOwnProperty("queryFieldNames")) {
				aliases = [];
				var fieldNames = mapLayer.getQueryFieldNames();
				var fieldAliases = mapLayer.getQueryFieldAliases();
				for (var j = 0; j < fieldNames.length; j++) {
					var alias = {
						"name" : fieldNames[j],
						"alias" : fieldAliases[j]
					};
					aliases.push(alias);
				}
			}
			var queryUrl = mapLayer.getQueryURL();

			var control = null;
			if (mapLayer.queriesWFS()) {

				queryUrl = queryUrl.trim();
				queryUrl = queryUrl.replace(/wms$/, "wfs");
				queryUrl = queryUrl + "?request=GetFeature&service=wfs&" + //
				"version=1.0.0&outputFormat=application/json&srsName=EPSG:900913&" + //
				"typeName=" + mapLayer.getServerLayerName() + "&propertyName=" + mapLayer.getQueryFieldNames().join(",");

				var wfsCallControl = null;

				control = new OpenLayers.Control();
				control.handler = new OpenLayers.Handler.Click(control, {
					'click' : function(e) {
						if (layerIdInfo.hasOwnProperty(layerInfo.getId()) && !layerIdInfo[layerInfo.getId()]["visibility"]) {
							return;
						}

						// bbox parameter
						var tolerance = 5;
						var point1 = map.getLonLatFromPixel(e.xy.offset({
							x : -tolerance,
							y : -tolerance
						}));
						var point2 = map.getLonLatFromPixel(e.xy.offset({
							x : tolerance,
							y : tolerance
						}));
						var bboxFilter = //
						"  <ogc:Intersects>" + //
						"    <ogc:PropertyName>" + mapLayer.getQueryGeomFieldName() + "</ogc:PropertyName>" + //
						// " <gml:Point xmlns:gml=\"http://www.opengis.net/gml\"
						// srsName=\"EPSG:900913\">" + //
						// " <gml:coordinates decimal=\".\" cs=\",\" ts=\" \">"
						// + point.lon + "," +
						// point.lat + "</gml:coordinates>" + //
						// " </gml:Point>" + //
						"    <gml:Box xmlns:gml=\"http://www.opengis.net/gml\" srsName=\"EPSG:900913\">" + //
						"      <gml:coordinates decimal=\".\" cs=\",\" ts=\" \">" + (point1.lon) + "," + (point1.lat) + " " + (point2.lon) + "," + (point2.lat) + "</gml:coordinates>" + //
						"    </gml:Box>" + //
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

						// time parameter
						var getFeatureMessage = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">";
						if (layerIdInfo.hasOwnProperty(layerInfo.getId()) && layerIdInfo[layerInfo.getId()].hasOwnProperty("timestamp")) {
							getFeatureMessage += //
							"  <ogc:And>" + //
							"" + bboxFilter + //
							"    <ogc:PropertyIsEqualTo>" + //
							"      <ogc:PropertyName>" + mapLayer.getQueryTimeFieldName() + "</ogc:PropertyName>" + //
							"      <ogc:Function name=\"dateParse\">" + //
							"        <ogc:Literal>yyyy-MM-dd</ogc:Literal>" + //
							"        <ogc:Literal>" + layerIdInfo[layerInfo.getId()]["timestamp"].toISO8601String() + "</ogc:Literal>" + //
							"      </ogc:Function>" + //
							"    </ogc:PropertyIsEqualTo>" + //
							"  </ogc:And>";
						} else {
							getFeatureMessage += bboxFilter;
						}
						getFeatureMessage += "</ogc:Filter>";
						var url = queryUrl + "&FILTER=" + encodeURIComponent(getFeatureMessage);

						if (wfsCallControl != null) {
							wfsCallControl.abort();
						}
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

									bus.send("info-features", [ mapLayer.getId(), features, e.xy.x, e.xy.y ]);
								}
							},
							controlCallBack : function(control) {
								wfsCallControl = control;
							},
							errorMsg : "Cannot get info for layer " + layerInfo.getName()
						});

					}
				});

			} else {
				var lastXY = null;

				var control = new OpenLayers.Control.WMSGetFeatureInfo({
					url : queryUrl,
					layerUrls : [ mapLayer.getBaseURL() ],
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
							if (evt.features && evt.features.length > 0 && lastXY.x == evt.xy.x && lastXY.y == evt.xy.y) {
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
										if (mapLayer.highlightBounds()) {
											feature.geometry = feature.geometry.getBounds().toGeometry();
										}
										feature.geometry.transform(epsg4326, epsg900913);
									}
									addBoundsAndHighlightGeom(feature);
								});

								bus.send("info-features", [ mapLayer.getId(), evt.features, evt.xy.x, evt.xy.y ]);
							}
						},
						beforegetfeatureinfo : function(event) {
							lastXY = event.xy;
							var id = mapLayer.getId();
							if (layerIdInfo.hasOwnProperty(layerInfo.getId()) && layerIdInfo[layerInfo.getId()].hasOwnProperty("timestamp")) {
								control.vendorParams = {
									"time" : layerIdInfo[layerInfo.getId()]["timestamp"].toISO8601String()
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
				layerIdInfo[layerInfo.getId()]["control"] = control;
				layerIdControl[mapLayer.getId()] = control;
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
