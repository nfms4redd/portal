define(["text!../layers.json"], function(layersjson) {

	var config = JSON.parse(layersjson);
		
	function find(arr, key, value, recurse) {
		for (var i = 0; i < arr.length; i++) {
			var el = arr[i];
			if (el.hasOwnProperty(key) && el[key] == value) {
				return el;
			} else if(recurse && el.hasOwnProperty(recurse)) {
				el = find(el[recurse], key, value, recurse);
				if (el) {
					return el;
				}
			}
		}
	};
	
	return {
		getPortalLayer: function(id) {
			return find(config.portalLayers, "id", id);
		},
		getWmsLayer: function(id) {
			return find(config.wmsLayers, "id", id);
		},
		getGroup: function(id) {
			return find(config.groups, "id", id, "items");
		}
	};

});
