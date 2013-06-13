package org.fao.unredd.layers;

import java.io.IOException;

import org.fao.unredd.layers.folder.InvalidFolderStructureException;

public interface LayerFactory {

	/**
	 * Returns an instance to manage the layer that is configured in GeoServer
	 * with the specified name. The name is qualified with the workspace name,
	 * like this: workspace_name:layer_name
	 * 
	 * @param layerName
	 * @return
	 * @throws NoSuchLayerException
	 *             If the specified layer is not in the system
	 */
	Layer newLayer(String layerName) throws NoSuchLayerException;

	/**
	 * @param layer
	 * @return
	 * @throws NoSuchLayerException
	 *             If the specified layer is not in the system
	 * @throws IOException
	 *             If there is any problem analyzing the folder
	 * @throws InvalidFolderStructureException
	 *             If the layer is not a mosaic layer
	 */
	MosaicLayer newMosaicLayer(String layer) throws NoSuchLayerException,
			InvalidFolderStructureException, IOException;

}
