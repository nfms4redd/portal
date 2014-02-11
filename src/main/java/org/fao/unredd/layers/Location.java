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
