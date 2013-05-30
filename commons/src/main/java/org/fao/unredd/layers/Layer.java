package org.fao.unredd.layers;

public interface Layer {

	Indicators getIndicators();

	Indicator getIndicator(String indicatorId);

}
