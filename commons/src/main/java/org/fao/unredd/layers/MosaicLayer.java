package org.fao.unredd.layers;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

import org.fao.unredd.layers.folder.InvalidFolderStructureException;

public interface MosaicLayer extends Layer {

	TreeMap<Date, File> getTimestamps(Location location)
			throws InvalidFolderStructureException, IOException;

}
