package org.fao.unredd.statsCalculator;

import java.io.File;

public interface GeoserverLayerFolderTranslator {

	File getLayerFolder(String layer)
			throws NotAMosaicException;

}
