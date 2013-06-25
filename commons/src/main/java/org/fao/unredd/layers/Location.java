package org.fao.unredd.layers;

import java.io.File;

public interface Location {
	String getGDALString();

	String getGDALFeatureName();

	/**
	 * @return a File instance if this location represents a file or a folder.
	 *         Null otherwise
	 */
	File getFile();

}
