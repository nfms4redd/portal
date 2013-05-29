package org.fao.unredd.statsCalculator;

import java.io.File;

/**
 * Transforms names of geoserver layers to their actual folder
 * 
 * @author fergonco
 */
public interface GeoserverLayerFolderTranslator {

	/**
	 * Return the Folder that stores the information for the specified layer
	 * 
	 * @param layer
	 * @return
	 * @throws NoSuchGeoserverLayerException
	 *             If there is no such layer in the geoserver instance
	 */
	File getLayerFolder(String layer) throws NoSuchGeoserverLayerException;

}
