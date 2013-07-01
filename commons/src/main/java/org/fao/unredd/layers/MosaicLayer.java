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
