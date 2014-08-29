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

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;

/**
 * Folder based implementation of {@link RWLayerFactory}
 * 
 * @author fergonco
 */
public class FolderLayerFactory implements LayerFactory {

	private File layerFolderRoot;

	public FolderLayerFactory(File layerFolderRoot) {
		this.layerFolderRoot = layerFolderRoot;
	}

	private void checkRoot() throws IOException {
		if (!layerFolderRoot.exists()) {
			throw new IOException("The layer folder root does not exist: "
					+ layerFolderRoot.getAbsolutePath());
		}
	}

	@Override
	public Layer newLayer(String layerName) throws IOException {
		checkRoot();
		return new LayerFolderImpl(layerName, getConfigurationFolder(layerName));
	}

	private File getConfigurationFolder(String layerName) throws IOException {
		File layerFolder = getLayerFolder(layerName);
		if (!layerFolder.exists() && !layerFolder.mkdirs()) {
			throw new IOException(
					"The folder does not exist and could not be created");
		}
		return layerFolder;
	}

	private File getLayerFolder(String layerName) {
		if (layerName.indexOf(':') == -1) {
			throw new IllegalArgumentException(
					"Layer name must have the ':' character separating workspace and layer name");
		}
		String[] workspaceAndName = layerName.split(Pattern.quote(":"));
		File workspaceFolder = new File(layerFolderRoot, workspaceAndName[0]);
		File layerFolder = new File(workspaceFolder, workspaceAndName[1]);
		return layerFolder;
	}

	@Override
	public boolean exists(String layerName) {
		return getLayerFolder(layerName).exists();
	}
}
