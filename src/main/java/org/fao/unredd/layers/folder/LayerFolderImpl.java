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
package org.fao.unredd.layers.folder;

import java.io.File;
import java.io.IOException;

import org.fao.unredd.layers.Layer;

/**
 * Concrete implementation of the {@link Layer} interface based on folders
 * 
 * @author fergonco
 */
public class LayerFolderImpl extends AbstractLayerFolder {

	/**
	 * Builds a new instance
	 * 
	 * @param folder
	 * @throws IOException
	 *             If the layer folder does not exist and cannot be created
	 */
	public LayerFolderImpl(String name, File folder)
			throws IllegalArgumentException, IOException {
		super(name, folder);
	}

}
