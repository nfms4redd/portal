package org.fao.unredd.layers;

import java.io.File;
import java.io.IOException;

/**
 * Represents the configuration for a layer. Layers have a name that can be used
 * to identify them in some catalog (typically, a GeoServer instance), it is
 * possible to associate temporal files in the work area that are persistent
 * over time and it is possible also to obtain configuration associated to the
 * layer
 * 
 * @author fergonco
 */
public interface Layer {

	/**
	 * Get a list of all the output identifiers in this layer
	 * 
	 * @return
	 * @throws IOException
	 */
	Outputs getOutputs() throws IOException;

	/**
	 * Get the output content with the specified id
	 * 
	 * @param outputId
	 * @return
	 * @throws NoSuchIndicatorException
	 */
	String getOutput(String outputId) throws NoSuchIndicatorException,
			IOException;

	/**
	 * Sets the content of the output with the specified id. If the output
	 * already exists it is overwritten, otherwise it is created. The output
	 * shall contain information for every object in the layer identified
	 * uniquely by the value of the field specified by fieldId.
	 * 
	 * @param id
	 * @param outputName
	 * @param fieldId
	 * @param content
	 * @throws IOException
	 */
	void setOutput(String id, String outputName, String fieldId, String content)
			throws IOException;

	/**
	 * Associates a file in the layer work area to the layer. The file is
	 * identified by the specified id. Every call to this method with the same
	 * parameters should result in the same File being returned.
	 * 
	 * @param id
	 * @return
	 */
	File getWorkFile(String id);

	/**
	 * Get the workspace part of the layer name. If {@link #getQualifiedName()}
	 * returns "work:lay", this method will return "work"
	 * 
	 * @return
	 */
	String getWorkspace();

	/**
	 * Get the name part of the layer name. If {@link #getQualifiedName()}
	 * returns "work:lay", this method will return "lay"
	 * 
	 * @return
	 */
	String getName();

	/**
	 * The same as {@link #getWorkspace()} + ":" + {@link #getName()}
	 * 
	 * @return
	 */
	String getQualifiedName();

	/**
	 * Gets the contents of a configuration item identified by the specified id
	 * 
	 * @param id
	 * @return
	 * @throws NoSuchConfigurationException
	 *             If the configuration item does not exist
	 * @throws IOException
	 *             If there is any problem accessing the configuration item
	 */
	String getConfiguration(String id) throws NoSuchConfigurationException,
			IOException;
}
