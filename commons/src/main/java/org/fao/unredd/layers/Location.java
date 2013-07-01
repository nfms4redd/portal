package org.fao.unredd.layers;

import java.io.File;
import java.io.IOException;

/**
 * Represents the place a layer is stored. It may reference a file, a folder, a
 * database table, etc.
 * 
 * @author fergonco
 */
public interface Location {

	/**
	 * Return the GDAL string to use to refer to this location in GDAL commands
	 * like ogr2ogr
	 * 
	 * @param passwordGetter
	 *            interface to ask for passwords if necessary
	 * @return
	 * @throws IOException
	 *             If there is any problem getting the password
	 */
	String getGDALString(PasswordGetter passwordGetter) throws IOException;

	/**
	 * Return the GDAL feature name to use to refer to this location in GDAL
	 * commands like ogr2ogr
	 * 
	 * @return
	 */
	String getGDALFeatureName();

	/**
	 * @return a File instance if this location represents a file or a folder.
	 *         Null otherwise
	 */
	File getFile();

}
