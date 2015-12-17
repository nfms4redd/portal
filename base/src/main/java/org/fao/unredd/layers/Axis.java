package org.fao.unredd.layers;

import java.util.Collection;
import java.util.LinkedHashMap;

public class Axis {

	private String label;
	private String units;
	private boolean opposite;
	private String type;
	private LinkedHashMap<String, Series> seriesList = new LinkedHashMap<String, Series>();

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public boolean isOpposite() {
		return opposite;
	}

	public void setOpposite(boolean opposite) {
		this.opposite = opposite;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Collection<Series> getSeries() {
		return seriesList.values();
	}

	public boolean allPositiveValues() {
		for (Series series : seriesList.values()) {
			if (!series.allPositiveValues()) {
				return false;
			}
		}
		return true;
	}

	public Series addSerie(String serieName) {
		Series series = seriesList.get(serieName);
		if (series == null) {
			series = new Series(serieName);
			seriesList.put(serieName, series);
		}

		return series;
	}
}