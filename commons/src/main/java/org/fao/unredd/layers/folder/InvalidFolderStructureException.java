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
