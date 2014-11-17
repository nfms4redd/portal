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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Output;
import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;

/**
 * Abstract {@link Layer} folder based implementation
 * 
 * @author fergonco
 */
public abstract class AbstractLayerFolder implements Layer {

	private static final String METADATA_FIELD_ID_PROPERTY_NAME = "field-id";
	private static final String METADATA_INDICATOR_NAME_PROPERTY_NAME = "indicator-name";
	private static final String METADATA_PROPERTIES_FILE_NAME = "metadata.properties";
	private static final String OUTPUT_FILE_NAME = "result.xml";
	private static final String OUTPUT = "output";
	private static final String CONFIGURATION = "configuration";
	private static final String WORK = "work";
	private File root;
	private String qName;

	public AbstractLayerFolder(String layerName, File root) throws IOException {
		String[] workspaceAndName = layerName.split(Pattern.quote(":"));
		if (workspaceAndName.length != 2) {
			throw new IllegalArgumentException(
					"The layer name must have the form workspaceName:layerName");
		}
		this.qName = layerName;
		this.root = root;
		if (!root.exists()) {
			if (!root.mkdirs()) {
				throw new IOException(
						"Layer folder doesn't exist and could not be created: "
								+ root);
			}
		}
	}

	public String getQualifiedName() {
		return qName;
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

	@Override
	public Outputs getOutputs() throws IOException {
		File outputFolder = getOutputFolder();
		if (!outputFolder.exists()) {
			return new Outputs();
		}
		File[] outputFolders = outputFolder.listFiles();
		ArrayList<Output> outputDescriptors = new ArrayList<Output>();
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
				outputDescriptors.add(new Output(folderName,
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
	public Output getOutput(String outputId) throws NoSuchIndicatorException,
			IOException {
		/*
		File outputFolder = new File(getOutputFolder(), outputId);
		if (!outputFolder.exists()) {
			throw new NoSuchIndicatorException(outputId);
		}
		InputStream input = new BufferedInputStream(new FileInputStream(
				new File(outputFolder, OUTPUT_FILE_NAME)));
		String ret = IOUtils.toString(input);
		input.close();
		*/
		return new Output(outputId, outputId, outputId);
	}
}
