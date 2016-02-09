define([ "layers-schema", "message-bus", "jquery", "jquery-ui" ], function(schema, bus, $) {

	var layerRoot = null;

	var dialog;
	var form;

	// Grab panel definitions
	var definitions = {
		"server" : {
			"default-server" : schema.properties["default-server"]
		},
		"toc" : schema.definitions.toc.properties,
		"portalLayer" : schema.definitions.portalLayer.allOf[1].properties,
		"wmsLayer-base" : schema.definitions["wmsLayer-base"].properties,
		"wmsLayer-wmsType" : schema.definitions["wmsLayer-wmsType"].allOf[1].properties,
		"wmsLayer-osmType" : schema.definitions["wmsLayer-osmType"].allOf[1].properties,
		"wmsLayer-gmapsType" : schema.definitions["wmsLayer-gmapsType"].allOf[1].properties
	};

	delete definitions.portalLayer.layers; // We assume 1:1 between portalLayer
	// and wmsLayer, so this is tricked
	delete definitions["wmsLayer-wmsType"].type; // Layer type is already
	// shown as an wmsLayer-base
	// property
	delete definitions["wmsLayer-osmType"].type; // Layer type is already
	// shown as an wmsLayer-base
	// property
	delete definitions["wmsLayer-gmapsType"].type; // Layer type is already
	// shown as an wmsLayer-base
	// property

	function editServer() {
		var form = createDialog("Edit Portal Properties", "portal"); // TODO
		// i18n
		addFields("Server", "server", form, {
			"default-server" : layerRoot.getDefaultServer()
		}); // TODO i18n
	}

	function editLayer(id) {
		form = createDialog("Edit Layer", function() { // TODO i18n
			saveLayer();
		});

		var portalValues = layerRoot.getPortalLayer(id);
		addPortalLayerFields(form, portalValues);

		var wmsValues = layerRoot.getWMSLayer(portalValues.layers[0]);
		addWmsLayerFields(form, wmsValues);
	}

	function editGroup(id) {
		form = createDialog("Edit Group", function() { // TODO i18n
			saveGroup();
		});

		var values = layerRoot.getGroup(id);
		addTocFields(form, values);
	}

	function newLayer(groupId) {
		form = createDialog("New Layer", function() { // TODO i18n
			addNewLayer(groupId);
		});

		var portalValues = {
			"id" : "unique-id-" + (new Date()).getTime(),
			"label" : "Nueva capa", // TODO i18n
			"active" : "true"
		};
		addPortalLayerFields(form, portalValues);

		var wmsValues = {};
		addWmsLayerFields(form, wmsValues);
	}

	function newGroup() {
		form = createDialog("New Group", function() { // TODO i18n
			addNewGroup();
		});

		var groupValues = {
			"id" : "unique-id-" + (new Date()).getTime(),
			"label" : "Nuevo grupo" // TODO i18n
		};
		addTocFields(form, groupValues);
	}

	function closeDialog() {
		dialog.dialog('close');
	}

	function createDialog(title, applyCallback) {
		dialog = $("<div/>");
		dialog.dialog({
			title : title,
			autoOpen : true,
			height : 600,
			minHeight : 400,
			width : 475,
			zIndex : 2000,
			resizable : true,
			closeOnEscape : false
		});

		var form = $("<form/>").addClass("layers-editor-form").appendTo(dialog);
		var cancelButton = $("<div/>").html("Cancel").appendTo(dialog); // TODO
		// i18n
		cancelButton.button().click(closeDialog);

		var applyButton = $("<div/>").html("Apply changes").appendTo(dialog); // TODO
		// i18n
		applyButton.button().click(function() {
			applyCallback();
			closeDialog();
		});
		return form;
	}

	function addTocFields(form, values) {
		addFields("Layer Description", "toc", form, values); // TODO i18n
	}

	function addPortalLayerFields(form, values) {
		addTocFields(form, values);
		addFields("Layer Properties", "portalLayer", form, values); // TODO i18n
	}

	function addWmsLayerFields(form, values) {
		var fieldset = addFields("Layer Datasource", "wmsLayer-base", form, values); // TODO
		// i18n

		fieldset.find("select[name=type]").change({
			form : form,
			values : values
		}, function(e) {
			setLayerType(this.value, e.data.form, e.data.values);
		});

		setLayerType(values.type || "wms", form, values);
	}

	function setLayerType(type, form, values) {
		var types = {
			wms : {
				label : "WMS", // TODO i18n
				definition : "wmsLayer-wmsType"
			},
			osm : {
				label : "OSM", // TODO i18n
				definition : "wmsLayer-osmType"
			},
			gmaps : {
				label : "Google Maps", // TODO i18n
				definition : "wmsLayer-gmapsType"
			}
		}
		for ( var t in types) {
			removePanel(types[t].definition, form);
		}
		addFields(types[type].label, types[type].definition, form, values);
	}

	function removePanel(name, form) {
		form.find("fieldset[class=" + name + "]").remove();
	}

	function addFields(title, panel, form, values) {
		var fieldset = $("<fieldset/>").addClass(panel).appendTo(form);
		$("<legend/>").text(title).appendTo(fieldset);

		for ( var name in definitions[panel]) {
			if (!definitions[panel][name].id) {
				definitions[panel][name].id = name;
			}
			addField(fieldset, definitions[panel][name], values[name]);
		}

		return fieldset;
	}

	function addField(form, definition, value) {
		var div = $("<div/>").appendTo(form);
		var label = $("<label/>").text(definition.title).appendTo(div);
		var input;

		if (definition.enum && definition.enum.length) {
			input = $("<select/>").attr("name", definition.id).appendTo(div);
			for ( var e in definition.enum) {
				var item = definition.enum[e];
				var option = $("<option>").attr("value", item).text(item).appendTo(input);
				if (item == value) {
					option.prop('selected', true);
				}
			}
		} else if (definition.type == "string") {
			input = $("<input/>").attr("name", definition.id).attr("type", "text").attr("value", value).appendTo(div);
		} else if (definition.type == "array") {
			var values = value ? value.join("\r\n") : "";
			var rows = value ? value.length + 1 : 3;
			input = $("<textarea/>").attr("name", definition.id).attr("rows", rows).val(values).appendTo(div);
		} else if (definition.type == "boolean") {
			input = $("<input/>").attr("name", definition.id).attr("type", "checkbox").appendTo(div);
			if (value) {
				input.prop('checked', true);
			}
		} else if (definition.hasOwnProperty("anyOf")) {
			// WARNING: Shitty code ahead. It works for "legend" and
			// "inlineLengendUrl",
			// but will probably misbehave in other "anyOf" definition.
			var chooser = $("<ul/>").attr("class", definition.id).appendTo(div);
			var alreadyChecked = false;
			for ( var i in definition.anyOf) {
				var el = $("<li/>").appendTo(chooser);
				var choiceDef = definition.anyOf[i];
				var choice = $("<input/>").attr("name", definition.id).attr("type", "radio").attr("value", i).appendTo(el);
				if ((choiceDef.hasOwnProperty("enum") && choiceDef.enum.indexOf(value) != -1)) {
					choice.prop('checked', true);
					alreadyChecked = true;
					addField(el, choiceDef, value);
				} else if (alreadyChecked) {
					addField(el, choiceDef, "");
				} else {
					choice.prop('checked', true);
					addField(el, choiceDef, value);
				}
			}
		} else {
			$("<span/>").addClass("layers-editor-type-not-implemented").text("Editor not implemented for this field type").appendTo(div); // TODO
			// i18n
		}
	}

	function getFormValues() {
		var data = {};
		var fieldsets = form.find("fieldset");

		// Process each of the fieldsets
		fieldsets.each(function(f, fieldset) {
			var panel = fieldset.className;
			var values = {};

			// Serialize all values except checkboxes (booleans)
			var arr = $(fieldset).find(":not(input[type=checkbox])").serializeArray();

			// Checkboxes have to be interpreted manually as booleans
			$(fieldset).find('input[type=checkbox]').each(function() {
				arr.push({
					name : this.name,
					value : this.checked
				});
			});

			// Get the values for each of the fields
			for ( var i in arr) {
				var field = arr[i];
				var name = field.name;
				var value = field.value;
				var definition = definitions[panel][name];

				if (definition.hasOwnProperty('enum')) {
					values[name] = value;
				} else if (definition.type == "string") {
					// No text => no key entry
					if (value.length > 0) {
						values[name] = value;
					}
				} else if (definition.type == "array") {
					// Split string by line
					values[name] = value.match(/[^\r\n]+/g);
					if (!value) {
						values[name] = [];
					}
				} else if (definition.hasOwnProperty("anyOf")) {
					value = $(fieldset).find('input[name=' + name + '][value=' + value + ']').next().find(':input').val();
					if (value.length > 0) {
						values[name] = value;
					}
				} else {
					// Default behaviour, assign the raw form value
					values[name] = value;
				}
			}
			data[panel] = values;
		});
		return data;
	}

	bus.listen("layers-loaded", function(e, newLayersRoot) {
		layerRoot = newLayersRoot;
	});

	function saveServer() {
		var data = getFormValues();
		layerRoot.setDefaultServer(data["server"]["default-server"]);
	}

	function saveGroup() {
		var data = getFormValues();
		layerRoot.getGroup(data.toc.id).merge(data.toc);
	}

	function addNewGroup() {
		var data = getFormValues();
		var group = $.extend({
			items : []
		}, data.toc);
		layerRoot.addGroup(group);
	}

	function buildWMSLayer() {
		var data = getFormValues();
		var wmsLayer = data["wmsLayer-base"];
		if (wmsLayer.type && wmsLayer.type == "osm") {
			$.extend(wmsLayer, data["wmsLayer-osmType"]);
		} else if (wmsLayer.type && wmsLayer.type == "gmaps") {
			$.extend(wmsLayer, data["wmsLayer-gmapsType"]);
		} else {
			$.extend(wmsLayer, data["wmsLayer-wmsType"]);
		}

		return wmsLayer;
	}

	function buildPortalLayer(wmsLayerId) {
		var data = getFormValues();
		return $.extend({
			layers : [ wmsLayerId ]
		}, data.toc, data.portalLayer);
	}

	function saveLayer() {
		var wmsLayer = buildWMSLayer();
		var portalLayer = buildPortalLayer(wmsLayer.id);
		layerRoot.getWMSLayer(wmsLayer.id).merge(wmsLayer);
		layerRoot.getPortalLayer(portalLayer.id).merge(portalLayer);
	}

	function addNewLayer(groupId) {
		var wmsLayer = buildWMSLayer();
		var portalLayer = buildPortalLayer(wmsLayer.id);

		layerRoot.addLayer(groupId, portalLayer, wmsLayer);
	}

	return {
		editServer : editServer,
		editLayer : editLayer,
		editGroup : editGroup,
		newLayer : newLayer,
		newGroup : newGroup
	};

});
