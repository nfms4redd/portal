package org.fao.unredd.layers.folder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
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
		if (!file.exists()) {
			throw new NoSuchConfigurationException();
		}
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
	public Outputs getOutputs() throws IOException {
		File outputFolder = getOutputFolder();
		if (!outputFolder.exists()) {
			return new Outputs();
		}
		File[] outputFolders = outputFolder.listFiles();
		Output[] outputs = new Output[outputFolders.length];
		for (int i = 0; i < outputs.length; i++) {
			String id = outputFolders[i].getName();
			try {
				outputs[i] = getOutput(id);
			} catch (NoSuchIndicatorException e) {
				throw new RuntimeException("bug");
			}
		}

		return new Outputs(outputs);
	}

	@Override
	public Output getOutput(String outputId) throws NoSuchIndicatorException,
			IOException {
		File outputFolder = new File(getOutputFolder(), outputId);
		if (!outputFolder.exists()) {
			throw new NoSuchIndicatorException(outputId);
		}
		InputStream input = new BufferedInputStream(new FileInputStream(
				new File(outputFolder, "content.html")));
		Output ret = new Output(outputId, outputId, "text/html",
				IOUtils.toString(input));
		input.close();
		return ret;
	}

	@Override
	public void setOutput(String id, String content) throws IOException {
		File outputFolder = new File(getOutputFolder(), id);
		if (outputFolder.exists()) {
			FileUtils.cleanDirectory(outputFolder);
		} else {
			if (!outputFolder.mkdirs()) {
				throw new IOException(
						"Cannot create the indicator output folder: "
								+ outputFolder.getAbsolutePath());
			}
		}

		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(new File(outputFolder, "raw-stats")));
		try {
			IOUtils.write(content, out);
		} finally {
			out.close();
		}
	}
}
