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
	 * Returns an instance to manage the layer configuration
	 * 
	 * @param layerName
	 * @return
	 * @throws IOException
	 *             If the layer folder does not exist and cannot be created
	 */
	Layer newLayer(String layerName) throws IOException;

	/**
	 * Checks if there is configuration for the layer with the specified name
	 * 
	 * @param layerName
	 * @return true if there is an entry for this layer, false otherwise
	 */
	boolean exists(String layerName);

}
