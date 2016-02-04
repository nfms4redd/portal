define([ "text!../layers.json", "message-bus" ], function(layersjson, bus) {

	var config = JSON.parse(layersjson);

	function getServer() {
		return config["default-server"];
	}

	function getPortalLayer(id) {
		return findById(config.portalLayers, "id", id);
	}

	function getWmsLayer(id) {
		return findById(config.wmsLayers, "id", id);
	}

	function getGroup(id) {
		return findById(config.groups, "id", id, "items");
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
		copy(portalLayer, portalLayerInput);

		upload(callback);
	}

	function addNewLayer(groupId, wmsLayerInput, portalLayerInput, callback) {
		var group = getGroup(groupId);
		group.items.push(portalLayerInput.id);
		config.wmsLayers.push(wmsLayerInput);
		config.portalLayers.push(portalLayerInput);

		upload(callback);
	}

	function doDeleteLayer(layerId) {
		for (var i = 0; i < config.portalLayers.length; i++) {
			var portalLayer = config.portalLayers[i];
			if (portalLayer.id == layerId) {
				config.portalLayers.splice(i, 1);
				if (portalLayer["layers"]) {
					for (var j = 0; j < portalLayer.layers.length; j++) {
						var wmsLayerId = portalLayer.layers[j];
						for (var k = 0; k < config.wmsLayers.length; k++) {
							if (config.wmsLayers[k].id == wmsLayerId) {
								config.wmsLayers.splice(k, 1);
								break;
							}
						}
					}
				}
				break;
			}
		}

	}

	function deleteLayer(layerId, callback) {
		doDeleteLayer(layerId);

		var group = find(config.groups, function(el) {
			return el["items"] && el["items"].indexOf(layerId) != -1;
		}, "items");
		group["items"].splice(group["items"].indexOf(layerId), 1);

		upload(callback);
	}

	function deleteAllGroupLayers(group) {
		for (var i = 0; i < group["items"].length; i++) {
			var groupItem = group["items"][i];
			if (typeof groupItem === 'string' || groupItem instanceof String) {
				doDeleteLayer(groupItem);
			} else if (groupItem["items"]) {
				deleteAllGroupLayers(groupItem);
			}
		}
	}

	function findAndDeleteGroup(array, groupId) {
		// Directly in the array
		for (var i = 0; i < array.length; i++) {
			if (array[i]["id"] == groupId) {
				deleteAllGroupLayers(array[i]);
				array.splice(i, 1);
				return true;
			}
		}

		// Delegate on each group
		for (var i = 0; i < array.length; i++) {
			if (array[i].hasOwnProperty("items")) {
				if (findAndDeleteGroup(array[i]["items"], groupId)) {
					return true;
				}
			}
		}

		return false;
	}

	function deleteGroup(groupId, callback) {
		if (findAndDeleteGroup(config.groups, groupId)) {
			upload(callback);
		}
	}

	function copy(target, source) {
		// Copy over new values
		for ( var name in source) {
			target[name] = source[name];
		}

		// Delete all properties not in source
		for ( var key in target) {
			if (!source.hasOwnProperty(key)) {
				delete target[key];
			}
		}

		return target;
	}

	function findById(arr, key, value, recurse) {
		return process(arr, function(el) {
			return el.hasOwnProperty(key) && el[key] == value;
		}, recurse);
	}

	function find(arr, test, recurse) {
		for (var i = 0; i < arr.length; i++) {
			var el = arr[i];
			if (test(el)) {
				return el;
			} else if (recurse && el.hasOwnProperty(recurse)) {
				el = find(el[recurse], test, recurse);
				if (el) {
					return el;
				}
			}
		}
	}

	function upload(onSuccess) {
		bus.send("ajax", {
			type : 'PUT',
			url : 'layers.json',
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(config, null, 4),
			success : function(data, textStatus, jqXHR) {
				if (onSuccess) {
					onSuccess.call(null, config);
				}
			},
			errorMsg : "Error uploading layers.json to the server" // TODO i18n
		});
	}

	return {
		getServer : getServer,
		getPortalLayer : getPortalLayer,
		getWmsLayer : getWmsLayer,
		getGroup : getGroup,
		updateServer : updateServer,
		updateGroup : updateGroup,
		updateLayer : updateLayer,
		deleteLayer : deleteLayer,
		deleteGroup : deleteGroup,
		addNewLayer : addNewLayer,
		updateGroups : updateGroups,
		getGroups : getGroups,
		root : config
	};
});
