package org.fao.unredd.statsCalculator;

import java.io.File;

/**
 * Interface to be notified when the execution of the statistics process with
 * concrete data is required.
 * 
 * Depending on the configuration of the layer, the creation of the outputs for
 * the statistical process may require the calculation of statistics several
 * times with different concrete data.
 * 
 * @author fergonco
 */
public interface CalculationListener {

	/**
	 * Create a table with a row for each province
	 * 
	 * @param areaRaster
	 *            Raster containing a numeric value in each sample that
	 *            indicates the area the sample covers
	 * @param mask
	 *            Raster containing 0 for non forest and 1 for forest
	 * @param classificationLayer
	 *            Layer containing the amount of
	 * @param classificationFieldName
	 */
	void calculate(File areaRaster, File mask, File classificationLayer,
			String classificationFieldName);

}
