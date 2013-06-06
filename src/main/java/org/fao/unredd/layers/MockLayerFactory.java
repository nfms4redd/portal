package org.fao.unredd.layers;

import java.io.File;

import org.fao.unredd.layers.folder.InvalidFolderStructureException;
import org.fao.unredd.layers.folder.LayerFolderImpl;

public class MockLayerFactory implements LayerFactory {

	@Override
	public Layer newLayer(String layerName) {
		try {
			return new LayerFolderImpl(
					new File(
							"/home/fergonco/java/nfms/nfms/mosaicStats/src/test/resources/okZonesSHP"));
		} catch (InvalidFolderStructureException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public MosaicLayer newMosaicLayer(String layer)
			throws NoSuchGeoserverLayerException {
		throw new UnsupportedOperationException();
	}

}
