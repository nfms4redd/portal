package org.fao.unredd.layers.folder;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.MosaicLayer;

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
	public MosaicLayer newMosaicLayer(String layerName)
			throws InvalidFolderStructureException, IOException {
		return new MosaicLayerFolder(layerName,
				getConfigurationFolder(layerName));
	}

	@Override
	public boolean exists(String layerName) {
		return getLayerFolder(layerName).exists();
	}

}
