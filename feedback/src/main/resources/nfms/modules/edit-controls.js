define([ "i18n", "openlayers" ], function(i18n) {

	var dragControl;

	/**
	 * @requires OpenLayers/Control/Panel.js
	 * @requires OpenLayers/Control/DrawFeature.js
	 * @requires OpenLayers/Control/ModifyFeature.js
	 * @requires OpenLayers/Control/WMSGetFeatureInfo.js
	 * @requires OpenLayers/Handler/Polygon.js
	 */
	/**
	 * Class: PortalToolbar The PortalToolbar is a panel to create, retrieve,
	 * update and delete contents in a polygon vector layer. It is used in
	 * feedback and custom statistics to draw the area of interest.
	 * 
	 * Inherits from: - <OpenLayers.Control.Panel>
	 */
	OpenLayers.Control.PortalToolbar = OpenLayers.Class(OpenLayers.Control.Panel, {

		citeCompliant : false,

		queryControl : null,

		initialize : function(layer, options) {
			OpenLayers.Control.Panel.prototype.initialize.apply(this, [ options ]);

			this.layer = layer;

			var drawControl = new OpenLayers.Control.DrawFeature(layer, OpenLayers.Handler.Polygon, {
				displayClass : 'olControlPortalToolbarAdd',
				title : i18n["feedback_addfeature_tooltip"],
				handlerOptions : {
					citeCompliant : this.citeCompliant
				}
			});

			var modifyControl = new OpenLayers.Control.ModifyFeature(layer, {
				displayClass : 'olControlPortalToolbarEdit',
				title : i18n["feedback_editfeature_tooltip"]
			});

			var controls = [ drawControl, modifyControl ];
			this.addControls(controls);
		},

		draw : function() {
			var div = OpenLayers.Control.Panel.prototype.draw.apply(this, arguments);
			if (this.defaultControl === null) {
				this.defaultControl = this.controls[0];
			}
			return div;
		},

		setQueryable : function(layer) {
			if (layer) {
				this.queryControl.layers = [ layer ];
				// show button
				var d = this.queryControl.panel_div;
				d.className = d.className.replace(/ItemHidden/, "ItemInactive");
			} else {
				if (this.queryControl.active) {
					this.activateControl(this.defaultControl);
				}
				// hide button
				var d = this.queryControl.panel_div;
				d.className = d.className.replace(/ItemInactive/, "ItemHidden");
			}
		},

		getFeaturesAsGeoJson : function() {
			var geoJsonString = new OpenLayers.Format.GeoJSON({
				'internalProjection' : this.layer.map.projection,
				'externalProjection' : this.layer.map.displayProjection
			}).write(this.getFeaturesAsMultipolygon());
			return new OpenLayers.Format.JSON().read(geoJsonString);
		},

		getFeaturesAsWKT : function() {
			var wktString = new OpenLayers.Format.WKT({
				'internalProjection' : this.layer.map.projection,
				'externalProjection' : this.layer.map.displayProjection
			}).write(this.getFeaturesAsMultipolygon());
			return wktString;
		},

		getFeaturesAsMultipolygon : function() {
			var polygons = [];
			for (i = 0; i < this.layer.features.length; i++) {
				polygons.push(this.layer.features[i].geometry);
			}
			var multiPolygon = new OpenLayers.Geometry.MultiPolygon(polygons);
			return new OpenLayers.Feature.Vector(multiPolygon);
		},
		
		hasFeatures : function() {
			return this.layer.features.length > 0;
		},

		CLASS_NAME : "OpenLayers.Control.PortalToolbar"
	});
});
