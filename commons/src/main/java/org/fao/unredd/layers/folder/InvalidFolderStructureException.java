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

/**
 * Signals that the layer folder does not have the expected structure
 * 
 * @author fergonco
 */
public class InvalidFolderStructureException extends Exception {
	private static final long serialVersionUID = 1L;
	private File offendingFile;

	public InvalidFolderStructureException(String msg, File offendingFile) {
		super(msg + ": " + offendingFile.getAbsolutePath());
		this.offendingFile = offendingFile;
	}

	/**
	 * Get the folder or file that does not fulfill the layer folder convention
	 * specification
	 * 
	 * @return
	 */
	public File getOffendingFile() {
		return offendingFile;
	}
}
