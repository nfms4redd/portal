package org.fao.unredd.layers;

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

}
