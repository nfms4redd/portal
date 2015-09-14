package org.fao.unredd.layers;

import java.util.ArrayList;

public class Series {

	private String name;
	private ArrayList<String> data = new ArrayList<String>();
	private boolean allPositiveValues = true;

	public Series(String serieName) {
		this.name = serieName;
	}

	public void addValue(Float value) {
		if (value == null) {
			data.add("null");
		} else {
			if (value < 0) {
				allPositiveValues = false;
			}
			data.add(value.toString());
		}
	}

	public String getName() {
		return name;
	}

	public boolean allPositiveValues() {
		return allPositiveValues;
	}

	public ArrayList<String> getData() {
		return data;
	}

}
