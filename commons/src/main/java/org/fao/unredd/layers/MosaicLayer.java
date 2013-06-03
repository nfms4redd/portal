package org.fao.unredd.layers;

import java.io.File;
import java.util.Date;
import java.util.TreeMap;

public interface MosaicLayer extends Layer {

	TreeMap<Date, File> getTimestamps();

}
