package org.fao.unredd.layers;

import java.io.File;
import java.io.IOException;

public interface Layer {

	/**
	 * Get a list of all the outputs identifiers in this layer
	 * 
	 * @return
	 */
	Outputs getOutputs();

	/**
	 * Get the output with the specified id
	 * 
	 * @param outputId
	 * @return
	 * @throws NoSuchIndicatorException
	 */
	Output getOutput(String outputId) throws NoSuchIndicatorException;

	/**
	 * Sets the content of the output with the specified id. If the output
	 * already exists it is overwritten, otherwise it is created
	 * 
	 * @param id
	 * @param content
	 * @throws IOException
	 */
	void setOutput(String id, String content) throws IOException;

	/**
	 * Associates a file in the work area to the layer. The file is identified
	 * by the specified id. Every call to this method with the same parameters
	 * should result in the same File being returned
	 * 
	 * @param id
	 * @return
	 */
	File getWorkFile(String id);

	/**
	 * Returns the folder where the data of this layer is stored as shapefiles.
	 * 
	 * @return
	 */
	File getDataFolder();

	/**
	 * Gets the contents of a configuration item
	 * 
	 * @param id
	 * @return
	 * @throws NoSuchConfigurationException
	 * @throws IOException
	 */
	String getConfiguration(String id) throws NoSuchConfigurationException,
			IOException;
}
