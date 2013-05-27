package org.fao.unredd.statsCalculator;

import java.io.File;

public class InvalidFolderStructureException extends Exception {
	private static final long serialVersionUID = 1L;
	private File offendingFile;

	public InvalidFolderStructureException(String msg, File offendingFile) {
		super(msg);
		this.offendingFile = offendingFile;
	}

	public File getOffendingFile() {
		return offendingFile;
	}
}
