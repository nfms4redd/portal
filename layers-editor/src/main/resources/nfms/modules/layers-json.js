define(["text!../layers.json", "message-bus"], function(layersjson, bus) {

	var config = JSON.parse(layersjson);

	function getServer() {
		return config["default-server"];
	}

	function getPortalLayer(id) {
		return find(config.portalLayers, "id", id);
	}

	function getWmsLayer(id) {
		return find(config.wmsLayers, "id", id);
	}

	function getGroup(id) {
		return find(config.groups, "id", id, "items");
	}

	function updateServer(url, callback) {
		config["default-server"] = url;

		upload(callback);
	}

	function updateGroup(input, callback) {
		var group = getGroup(input.id);
		copy(group, input);

		upload(callback);
	}

	function getGroups() {
		return config.groups;
	}

	function updateGroups(groups, callback) {
		config.groups = groups;

		upload(callback);
	}

	function updateLayer(wmsLayerInput, portalLayerInput, callback) {
		var wmsLayer = getWmsLayer(wmsLayerInput.id);
		copy(wmsLayer, wmsLayerInput);

		var portalLayer = getPortalLayer(portalLayerInput.id);
		copy(portalLayer,  portalLayerInput);

		upload(callback);
	}

	function addNewLayer(groupId, wmsLayerInput, portalLayerInput, callback) {
		var group = getGroup(groupId);
		group.items.push(portalLayerInput.id);
		config.wmsLayers.push(wmsLayerInput);
		config.portalLayers.push(portalLayerInput);

		upload(callback);
	}

	function copy(target, source) {
		// Copy over new values
		for (var name in source) {
			target[name] = source[name];
		}

		// Delete all properties not in source
		for (var key in target) {
			if(!source.hasOwnProperty(key)) {
				delete target[key];
			}
		}

		return target;
	}

	function find(arr, key, value, recurse) {
		for (var i = 0; i < arr.length; i++) {
			var el = arr[i];
			if (el.hasOwnProperty(key) && el[key] == value) {
				return el;
			} else if (recurse && el.hasOwnProperty(recurse)) {
				el = find(el[recurse], key, value, recurse);
				if (el) {
					return el;
				}
			}
		}
	}

	function upload(onSuccess) {
		bus.send("ajax", {
			type: 'PUT',
			url: 'layers.json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(config, null, 4),
			success: function(data, textStatus, jqXHR) {
				if (onSuccess) {
					onSuccess.call(null, config);
				}
			},
			errorMsg: "Error uploading layers.json to the server" // TODO i18n
		});
	}

	return {
		getServer: getServer,
		getPortalLayer: getPortalLayer,
		getWmsLayer: getWmsLayer,
		getGroup: getGroup,
		updateServer: updateServer,
		updateGroup: updateGroup,
		updateLayer: updateLayer,
		addNewLayer: addNewLayer,
		updateGroups: updateGroups,
		getGroups: getGroups,
		root: config
	};
});
