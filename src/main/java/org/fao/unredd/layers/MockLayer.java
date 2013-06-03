package org.fao.unredd.layers;

import java.io.File;

public class MockLayer implements Layer {

	private Indicator indicator = new Indicator("zonal-stats", "Statistics",
			"text/html", "These are the statistics maaaaan");

	@Override
	public Indicators getIndicators() {
		return new Indicators(indicator);
	}

	@Override
	public Indicator getIndicator(String indicatorId) {
		return indicator;
	}

	@Override
	public File getWorkFile(String id) {
		throw new UnsupportedOperationException();
	}

}
