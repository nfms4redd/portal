package org.fao.unredd.layers;

public class MockLayer implements Layer {

	@Override
	public Indicator[] getIndicators() {
		return new Indicator[] { new Indicator("zonal-stats", "Statistics") };
	}

}
