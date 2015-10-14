define(["layers-json", "layers-schema", "jquery", "jquery-ui"], function(layers, schema, $) {

	function editLayer(id) {
		var form = createForm("Edit Layer");

		var portalValues = layers.getPortalLayer(id);
		addPortalLayerFields(form, portalValues);

		var wmsValues = layers.getWmsLayer(portalValues.layers[0]);
		addWmsLayerFields(form, wmsValues);
	}

	function editGroup(id) {
		var form = createForm("Edit Group");

		var values = layers.getGroup(id);
		addTocFields(form, values);
	}

	function createForm(title) {
		var dialog = $("<div/>");
		dialog.dialog({
			title: title,
			autoOpen: true,
			height: 600,
			minHeight: 400,
			width: 475,
			zIndex: 2000,
			resizable: true,
			closeOnEscape: false
		});

		var form = $("<form/>").addClass("layers-editor-form").appendTo(dialog);
		return form;
	}

	function addTocFields(form, values) {
		var properties = schema.definitions.toc.properties;
		addFields("Layer Switcher Entry", form, properties, values);
	}

	function addPortalLayerFields(form, values) {
		addTocFields(form, values);
		var properties = schema.definitions.portalLayer.allOf[1].properties;
		delete properties.layers;
		addFields("Portal Layer", form, properties, values);
	}

	function addWmsLayerFields(form, values) {
		var properties = schema.definitions["wmsLayer-base"].properties;
		addFields("Pseudo-WMS Layer", form, properties, values);

		if (properties.type && properties.type == "osm") {
			properties = schema.definitions["wmsLayer-osmType"].allOf[1].properties;
			addFields("OSM Layer", form, properties, values);
		} else if (properties.type && properties.type == "gmaps") {
			properties = schema.definitions["wmsLayer-gmapsType"].allOf[1].properties;
			addFields("Google Maps Layer", form, properties, values);
		} else {
			properties = schema.definitions["wmsLayer-wmsType"].allOf[1].properties;
			delete properties.type;
			addFields("True-WMS Layer", form, properties, values);
		}
	}

	function addFields(title, form, properties, values) {
		var fieldset = $("<fieldset/>").appendTo(form);
		$("<legend/>").text(title).appendTo(fieldset);

		for (var i in properties) {
			if (!properties[i].id) {
				properties[i].id = i;
			}
			addField(fieldset, properties[i], values[i]);
		}
	}

	function addField(form, schema, value) {
		var div = $("<div/>").appendTo(form);
		var label = $("<label/>").text(schema.title).appendTo(div);

		if (schema.enum && schema.enum.length) {
			var input = $("<select/>").attr("id", schema.id).appendTo(div);
			for (var i in schema.enum) {
				var value = schema.enum[i];
				$("<option>").attr("value", value).text(value).appendTo(input);
			}
		} else if (schema.type == "string") {
			var input = $("<input/>").attr("id", schema.id).attr(
					"type", "text").attr("value", value).appendTo(div);
		} else if (schema.type == "array") {
			if (Object.prototype.toString.call(value) === '[object Array]') {
				value = value.join(",");
			}
			var input = $("<input/>").attr("id", schema.id).attr("type", "text").attr("value", value).appendTo(div);
		} else if (schema.type == "boolean") {
			var input = $("<input/>").attr("id", schema.id).attr("type", "checkbox").attr("value", value).appendTo(div);
			if (value == true) {
				input.prop('checked', true);
			}
		} else {
			$("<span/>").addClass("fieldtypeeditornotimplemented").text("Field type editor not implemented").appendTo(div);
		}
	}

	return {
		editLayer: editLayer,
		editGroup: editGroup
	};

});
