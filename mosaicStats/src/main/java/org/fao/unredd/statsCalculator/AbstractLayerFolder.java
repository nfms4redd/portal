package org.fao.unredd.statsCalculator;

import java.io.File;

/**
 * Abstract class with the common folder structure
 * 
 * @author fergonco
 */
public abstract class AbstractLayerFolder {

	private static final String OUTPUT = "output";
	private static final String DATA = "data";
	private static final String CONFIGURATION = "configuration";
	private static final String WORK = "work";
	private File root;

	public AbstractLayerFolder(File root) {
		this.root = root;
		if (!root.exists()) {
			throw new IllegalArgumentException("The folder does not exist: "
					+ root.getAbsolutePath());
		}
	}

	public File getDataFolder() {
		return new File(root, DATA);
	}

	public File getWorkFolder() {
		return new File(root, WORK);
	}

	/**
	 * Convenience method to get a file on the work folder
	 * 
	 * @param fileName
	 * @return
	 */
	public File getWorkFile(String fileName) {
		return new File(getWorkFolder(), fileName);
	}

	public File getConfigurationFolder() {
		return new File(root, CONFIGURATION);
	}

	public File getOutputFolder() {
		return new File(root, OUTPUT);
	}
}
