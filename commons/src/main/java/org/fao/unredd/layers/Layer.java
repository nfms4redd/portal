package org.fao.unredd.layers;

import java.io.File;

public interface Layer {

	Indicators getIndicators();

	Indicator getIndicator(String indicatorId) throws NoSuchIndicatorException;

	/**
	 * Associates a file in the work area to the layer. The file is identified
	 * by the specified id. Every call to this method with the same parameters
	 * should result in the same File being returned
	 * 
	 * @param id
	 * @return
	 */
	File getWorkFile(String id);
}
