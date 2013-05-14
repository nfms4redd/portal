package org.fao.unredd.statsCalculator;

import java.io.File;

public interface CalculationListener {

	void calculate(File areaRaster, File mask, String classificationLayer,
			String classificationFieldName);

}
