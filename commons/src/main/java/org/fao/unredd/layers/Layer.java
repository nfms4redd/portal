package org.fao.unredd.layers;

import java.io.File;
import java.io.IOException;

public interface Layer {

	Outputs getOutputs();

	Output getOutput(String outputId) throws NoSuchIndicatorException;

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
