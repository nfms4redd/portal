package org.fao.unredd.layers.folder;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.MosaicLayer;
import org.fao.unredd.layers.NoSuchLayerException;

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
	public Layer newLayer(String layerName) throws NoSuchLayerException {
		return new LayerFolderImpl(getFolder(layerName));
	}

	private File getFolder(String layerName) throws NoSuchLayerException {
		String[] workspaceAndName = layerName.split(Pattern.quote(":"));
		File workspaceFolder = new File(layerFolderRoot, workspaceAndName[0]);
		File layerFolder = new File(workspaceFolder, workspaceAndName[1]);
		if (!layerFolder.exists()) {
			throw new NoSuchLayerException(layerName);
		}
		return layerFolder;
	}

	@Override
	public MosaicLayer newMosaicLayer(String layerName)
			throws NoSuchLayerException, InvalidFolderStructureException,
			IOException {
		return new MosaicLayerFolder(getFolder(layerName));
	}

}
