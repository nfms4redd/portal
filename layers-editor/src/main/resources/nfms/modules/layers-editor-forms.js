define(["layers-json", "layers-schema", "jquery", "jquery-ui"], function(layers, schema, $) {

	function editLayer(id) {
		var form = createForm("Edit Layer");

		var portalValues = layers.getPortalLayer(id);
		addPortalLayerFields(form, portalValues);

		var wmsValues = layers.getWmsLayer(portalValues.layers[0]);
		addWmsLayerFields(form, wmsValues);

		getFormValues(form);
	}

	function editGroup(id) {
		var form = createForm("Edit Group");

		var values = layers.getGroup(id);
		addTocFields(form, values);

		getFormValues(form);
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
		addFields("Layer Tree Item", "toc", form, properties, values);
	}

	function addPortalLayerFields(form, values) {
		addTocFields(form, values);
		var properties = schema.definitions.portalLayer.allOf[1].properties;
		delete properties.layers;
		addFields("Portal Layer", "portalLayer", form, properties, values);
	}

	function addWmsLayerFields(form, values) {
		var properties = schema.definitions["wmsLayer-base"].properties;
		addFields("Pseudo-WMS Layer", "wmsLayer-base", form, properties, values);

		if (properties.type && properties.type == "osm") {
			properties = schema.definitions["wmsLayer-osmType"].allOf[1].properties;
			delete properties.type;
			addFields("OSM Layer", "wmsLayer-osmType", form, properties, values);
		} else if (properties.type && properties.type == "gmaps") {
			properties = schema.definitions["wmsLayer-gmapsType"].allOf[1].properties;
			delete properties.type;
			addFields("Google Maps Layer", "wmsLayer-gmapsType", form, properties, values);
		} else {
			properties = schema.definitions["wmsLayer-wmsType"].allOf[1].properties;
			delete properties.type;
			addFields("True-WMS Layer", "wmsLayer-wmsType", form, properties, values);
		}
	}

	function addFields(title, id, form, properties, values) {
		var fieldset = $("<fieldset/>").addClass(id).appendTo(form);
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
			var input = $("<select/>").attr("name", schema.id).appendTo(div);
			for (var i in schema.enum) {
				var item = schema.enum[i];
				var option = $("<option>").attr("value", item).text(item).appendTo(input);
				if (item == value) {
					option.prop('selected', true);
				}
			}
		} else if (schema.type == "string") {
			var input = $("<input/>").attr("name", schema.id).attr(
					"type", "text").attr("value", value).appendTo(div);
		} else if (schema.type == "array") {
			var values = value ? value.join("\r\n"): "";
			var rows = value ? value.length + 1 : 3;
			var input = $("<textarea/>").attr("name", schema.id).attr("rows", rows).val(values).appendTo(div);
		} else if (schema.type == "boolean") {
			var input = $("<input/>").attr("name", schema.id).attr("type", "checkbox").attr("value", value).appendTo(div);
			if (value == true) {
				input.prop('checked', true);
			}
		} else if (schema.hasOwnProperty("anyOf")) {
			// WARNING: Shitty code ahead. It works for "legend" and "inlineLengendUrl",
			// but will probably misbehave in other "anyOf" schema definitions.
			var chooser = $("<ul/>").attr("class", schema.id).appendTo(div);
			var alreadyChecked = false;
			for(var i in schema.anyOf) {
				var el = $("<li/>").appendTo(chooser);
				var choiceSchema = schema.anyOf[i];
				var choice = $("<input/>").attr("name", schema.id).attr("type", "radio").attr("value", i).appendTo(el);
				if((choiceSchema.hasOwnProperty("enum") && choiceSchema.enum.indexOf(value) != -1)) {
					choice.prop('checked', true);
					alreadyChecked = true;
					addField(el, choiceSchema, value);
				} else if(alreadyChecked) {
					addField(el, choiceSchema, "");
				} else {
					choice.prop('checked', true);
					addField(el, choiceSchema, value);
				}
			}
		} else {
			$("<span/>").addClass("layers-editor-type-not-implemented").text("Field type editor not implemented").appendTo(div);
		}
	}

	function getFormValues(form) {
		var data = {};
		form.find("fieldset").each(function(){
			var group = this.className;
			var values = {};
			var arr = $(this).serializeArray();
			for(var i in arr) {
				var field = arr[i];
				if(field.value) {
					values[field.name] = field.value;
				}
			}
			data[group] = values;
		});
		console.log(data);
		return data;
	}

	return {
		editLayer: editLayer,
		editGroup: editGroup
	};

});
