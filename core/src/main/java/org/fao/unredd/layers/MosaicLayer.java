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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

import org.fao.unredd.layers.folder.InvalidFolderStructureException;

/**
 * Special interface for mosaic layers to obtain a sorted map with the
 * timestamps
 * 
 * @author fergonco
 */
public interface MosaicLayer extends Layer {

	/**
	 * Obtains the timestamps of the mosaic folder in a map from the date to the
	 * file containing the actual snapshot
	 * 
	 * @param location
	 * @return
	 * @throws InvalidFolderStructureException
	 *             If the location does not point to a image mosaic folder
	 * @throws IOException
	 *             If there is any problem analyzing the folder
	 */
	TreeMap<Date, File> getTimestamps(Location location)
			throws InvalidFolderStructureException, IOException;

}
