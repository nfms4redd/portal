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
import org.fao.unredd.layers.MosaicLayer;

/**
 * Folder based implementation of {@link LayerFactory}
 * 
 * @author fergonco
 */
public class FolderLayerFactory implements LayerFactory {

	private File layerFolderRoot;

	public FolderLayerFactory(File layerFolderRoot) {
		if (!layerFolderRoot.exists()) {
			throw new IllegalArgumentException(
					"The layer folder root does not exist: "
							+ layerFolderRoot.getAbsolutePath());
		}
		this.layerFolderRoot = layerFolderRoot;
	}

	@Override
	public Layer newLayer(String layerName) throws IOException {
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
		String[] workspaceAndName = layerName.split(Pattern.quote(":"));
		File workspaceFolder = new File(layerFolderRoot, workspaceAndName[0]);
		File layerFolder = new File(workspaceFolder, workspaceAndName[1]);
		return layerFolder;
	}

	@Override
	public MosaicLayer newMosaicLayer(String layerName) throws IOException {
		return new MosaicLayerFolder(layerName,
				getConfigurationFolder(layerName));
	}

	@Override
	public boolean exists(String layerName) {
		return getLayerFolder(layerName).exists();
	}

}
