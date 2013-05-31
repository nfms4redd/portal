package org.fao.unredd.layers;

public interface LayerFactory {

	/**
	 * Returns an instance to manage the layer that is configured in GeoServer
	 * with the specified name. The name is qualified with the workspace name,
	 * like this: workspace_name:layer_name
	 * 
	 * @param layerName
	 * @return
	 * @throws NoSuchGeoserverLayerException
	 *             If the specified layer is not in the instance of geoserver
	 */
	Layer newLayer(String layerName) throws NoSuchGeoserverLayerException;

}
