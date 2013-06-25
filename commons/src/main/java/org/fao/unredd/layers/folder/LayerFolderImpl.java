package org.fao.unredd.layers.folder;

import java.io.File;
import java.io.IOException;

import org.fao.unredd.layers.Layer;

/**
 * Basic concrete implementation of the {@link Layer} interface based on folders
 * 
 * @author fergonco
 */
public class LayerFolderImpl extends AbstractLayerFolder {

	/**
	 * Builds a new instance
	 * 
	 * @param folder
	 * @throws IOException
	 *             If the layer folder does not exist and cannot be created
	 */
	public LayerFolderImpl(String name, File folder)
			throws IllegalArgumentException, IOException {
		super(name, folder);
	}

}
