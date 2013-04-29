/**
 * @requires OpenLayers/Control/Panel.js
 * @requires OpenLayers/Control/DrawFeature.js
 * @requires OpenLayers/Control/ModifyFeature.js
 * @requires OpenLayers/Control/WMSGetFeatureInfo.js
 * @requires OpenLayers/Handler/Polygon.js
 */

/**
 * Class: PortalToolbar 
 * The PortalToolbar is a panel to create, retrieve, update and delete contents in a polygon vector layer.
 * It is used in feedback and custom statistics to draw the area of interest.
 * 
 * Inherits from:
 *  - <OpenLayers.Control.Panel>
 */
OpenLayers.Control.PortalToolbar = OpenLayers.Class(
  OpenLayers.Control.Panel, {

    citeCompliant: false,
    
    queryControl: null,

    initialize: function(layer, options) {
        OpenLayers.Control.Panel.prototype.initialize.apply(this, [options]);
        
        this.layer = layer;
        
        this.queryControl = new OpenLayers.Control.WMSGetFeatureInfo({
            displayClass: 'olControlPortalToolbarGet',
            title: messages.feedback_query_tooltip,
            queryVisible: true,
            infoFormat: 'application/vnd.ogc.gml',
            hover: false,
            drillDown: false,
            maxFeatures: 5,
            handlerOptions: {
                "click": {
                    'single': true,
                    'double': false
                }
            },
            eventListeners: {
                getfeatureinfo: function (evt) {
                    if (evt.features && evt.features.length) {
                    	for(i in evt.features) {
                    		evt.features[i].geometry.transform(this.layer.map.displayProjection, this.layer.map.projection);
                    	}
                    	this.layer.addFeatures(evt.features);
                    }
                },
                scope: this
            },
            formatOptions: {
                typeName: 'XXX', featureNS: 'http://www.openplans.org/unredd'
            }
        });
        
        var controls = [
            new OpenLayers.Control.DrawFeature(layer, OpenLayers.Handler.Polygon, {
                displayClass: 'olControlPortalToolbarAdd',
                title: messages.feedback_addfeature_tooltip,
                handlerOptions: {citeCompliant: this.citeCompliant}
            }),
            new OpenLayers.Control.ModifyFeature(layer, {
            	displayClass: 'olControlPortalToolbarEdit',
                title: messages.feedback_editfeature_tooltip,
                handleKeypress: function(evt) {
                    var code = evt.keyCode;
                    
                    // check for delete key
                    if(this.feature &&
                       OpenLayers.Util.indexOf(this.deleteCodes, code) != -1) {
                        var vertex = this.dragControl.feature;
                        if(vertex &&
                           OpenLayers.Util.indexOf(this.vertices, vertex) != -1 &&
                           !this.dragControl.handlers.drag.dragging &&
                           vertex.geometry.parent) {
                            // remove the vertex
                            vertex.geometry.parent.removeComponent(vertex.geometry);
                            this.layer.events.triggerEvent("vertexremoved", {
                                vertex: vertex.geometry,
                                feature: this.feature,
                                pixel: evt.xy
                            });
                            this.layer.drawFeature(this.feature, this.standalone ?
                                                   undefined :
                                                   this.selectControl.renderIntent);
                            this.modified = true;
                            this.resetVertices();
                            this.setFeatureState();
                            this.onModification(this.feature);
                            this.layer.events.triggerEvent("featuremodified", 
                                                           {feature: this.feature});
                        } else if (confirm(messages.feedback_deletepolygon_confirm)) {
                        	this.feature.destroy();
                        	this.feature = null;
                        	this.resetVertices();
                        } 
                    }
                }
            }),
            this.queryControl
        ];
        this.addControls(controls);
    },

    draw: function() {
        var div = OpenLayers.Control.Panel.prototype.draw.apply(this, arguments);
        if (this.defaultControl === null) {
            this.defaultControl = this.controls[0];
        }
        return div;
    },
    
    setQueryable: function(layer) {
    	if(layer) {
    		this.queryControl.layers = [layer];
    		// show button
    		var d = this.queryControl.panel_div;
    		d.className = d.className.replace(/ItemHidden/, "ItemInactive");
    	} else {
    		if(this.queryControl.active) {
    			this.activateControl(this.defaultControl);
    		}
    		// hide button
   			var d = this.queryControl.panel_div;
   			d.className = d.className.replace(/ItemInactive/, "ItemHidden");
    	}
    },
    
    getFeaturesAsGeoJson: function() {
    	var geoJsonString = new OpenLayers.Format.GeoJSON({
            'internalProjection': this.layer.map.projection,
            'externalProjection': this.layer.map.displayProjection
        }).write(this.getFeaturesAsMultipolygon());
    	return new OpenLayers.Format.JSON().read(geoJsonString);
    },
    
    getFeaturesAsWKT: function() {
    	var wktString = new OpenLayers.Format.WKT({
            'internalProjection': this.layer.map.projection,
            'externalProjection': this.layer.map.displayProjection    		
    	}).write(this.getFeaturesAsMultipolygon());
    	return wktString;
    },
    
    getFeaturesAsMultipolygon: function() {
        var polygons = [];
        for (i = 0; i < this.layer.features.length; i++) {
           polygons.push(this.layer.features[i].geometry);
        }
        var multiPolygon = new OpenLayers.Geometry.MultiPolygon(polygons);
        return new OpenLayers.Feature.Vector(multiPolygon);
    },

    CLASS_NAME: "OpenLayers.Control.PortalToolbar"
});
