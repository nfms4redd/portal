/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.layers;

import java.io.IOException;

import javax.naming.ConfigurationException;

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
	 * @throws ConfigurationException
	 */
	boolean exists(String layerName) throws ConfigurationException;
}
