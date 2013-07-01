package org.fao.unredd.layers;

import java.io.IOException;

/**
 * Builds instances to manage the layer configurations. Layer names are
 * qualified with the workspace name like this: workspace_name:layer_name
 * 
 * @author fergonco
 */
public interface LayerFactory {

	/**
	 * Returns an instance to manage the layer configuration, building the
	 * folder for its configuration if necessary
	 * 
	 * @param layerName
	 * @return
	 * @throws IOException
	 *             If the layer folder does not exist and cannot be created
	 */
	Layer newLayer(String layerName) throws IOException;

	/**
	 * Creates a new layer, building the folder for it's configuration if
	 * necessary
	 * 
	 * @param layer
	 * @return
	 * @throws IOException
	 *             If the layer configuration does not exist and cannot be
	 *             created or there is any problem analyzing it
	 */
	MosaicLayer newMosaicLayer(String layer) throws IOException;

	/**
	 * Checks if the layer configuration exists
	 * 
	 * @param layerName
	 * @return true if there is an entry for this layer, false otherwise
	 */
	boolean exists(String layerName);
}
