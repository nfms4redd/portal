package org.fao.unredd.statsCalculator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchConfigurationException;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Output;
import org.fao.unredd.layers.Outputs;

/**
 * Abstract class with the common folder structure
 * 
 * @author fergonco
 */
public abstract class AbstractLayerFolder implements Layer {

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

	@Override
	public String getConfiguration(String id)
			throws NoSuchConfigurationException, IOException {
		File file = new File(getConfigurationFolder(), id);
		BufferedInputStream input = new BufferedInputStream(
				new FileInputStream(file));
		String ret;
		try {
			ret = new String(IOUtils.toByteArray(input));
		} finally {
			input.close();
		}
		return ret;
	}

	public File getConfigurationFolder() {
		return new File(root, CONFIGURATION);
	}

	public File getOutputFolder() {
		return new File(root, OUTPUT);
	}

	@Override
	public Outputs getOutputs() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Output getOutput(String outputId) throws NoSuchIndicatorException {
		throw new UnsupportedOperationException();
	}
}
