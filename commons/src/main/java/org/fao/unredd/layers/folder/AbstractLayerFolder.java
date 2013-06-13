package org.fao.unredd.layers.folder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchConfigurationException;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;

/**
 * Abstract class with the common folder structure
 * 
 * @author fergonco
 */
public abstract class AbstractLayerFolder implements Layer {

	private static final String METADATA_FIELD_ID_PROPERTY_NAME = "field-id";
	private static final String METADATA_INDICATOR_NAME_PROPERTY_NAME = "indicator-name";
	private static final String METADATA_PROPERTIES_FILE_NAME = "metadata.properties";
	private static final String OUTPUT_FILE_NAME = "result.xml";
	private static final String OUTPUT = "output";
	private static final String DATA = "data";
	private static final String CONFIGURATION = "configuration";
	private static final String WORK = "work";
	private File root;

	public AbstractLayerFolder(File root) throws IllegalArgumentException {
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
		ArrayList<OutputDescriptor> outputDescriptors = new ArrayList<OutputDescriptor>();
		for (int i = 0; i < outputFolders.length; i++) {
			String folderName = outputFolders[i].getName();
			File outputRoot = new File(getOutputFolder(), folderName);
			Properties metadata = getMetadataProperties(outputRoot);
			String fieldId = metadata
					.getProperty(METADATA_FIELD_ID_PROPERTY_NAME);
			String indicatorName = metadata
					.getProperty(METADATA_INDICATOR_NAME_PROPERTY_NAME);
			if (fieldId != null
					&& new File(outputRoot, OUTPUT_FILE_NAME).exists()) {
				outputDescriptors.add(new OutputDescriptor(folderName,
						indicatorName, fieldId));
			}
		}

		return new Outputs(outputDescriptors);
	}

	private Properties getMetadataProperties(File outputRoot)
			throws IOException, FileNotFoundException {
		Properties metadata = new Properties();
		File metadataFile = getMetadataFile(outputRoot);
		if (metadataFile.exists()) {
			metadata.load(new FileInputStream(metadataFile));
		}
		return metadata;
	}

	private File getMetadataFile(File outputRoot) {
		return new File(outputRoot, METADATA_PROPERTIES_FILE_NAME);
	}

	@Override
	public String getOutput(String outputId) throws NoSuchIndicatorException,
			IOException {
		File outputFolder = new File(getOutputFolder(), outputId);
		if (!outputFolder.exists()) {
			throw new NoSuchIndicatorException(outputId);
		}
		InputStream input = new BufferedInputStream(new FileInputStream(
				new File(outputFolder, OUTPUT_FILE_NAME)));
		String ret = IOUtils.toString(input);
		input.close();
		return ret;
	}

	@Override
	public void setOutput(String id, String outputName, String fieldId,
			String content) throws IOException {
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

		BufferedOutputStream resultStream = new BufferedOutputStream(
				new FileOutputStream(new File(outputFolder, OUTPUT_FILE_NAME)));
		try {
			IOUtils.write(content, resultStream);
		} finally {
			resultStream.close();
		}

		Properties metadata = getMetadataProperties(outputFolder);
		metadata.setProperty(METADATA_FIELD_ID_PROPERTY_NAME, fieldId);
		metadata.setProperty(METADATA_INDICATOR_NAME_PROPERTY_NAME, outputName);
		FileOutputStream metadataStream = new FileOutputStream(
				getMetadataFile(outputFolder));
		try {
			metadata.store(metadataStream, "");
		} finally {
			metadataStream.close();
		}
	}
}
