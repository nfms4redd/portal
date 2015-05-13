package org.fao.unredd.layers;

import java.util.ArrayList;

public class Series {

	private String name;
	private ArrayList<Float> data = new ArrayList<Float>();

	public Series(String serieName) {
		this.name = serieName;
	}

	public void addValue(float value) {
		data.add(value);
	}

	public String getName() {
		return name;
	}

	public ArrayList<Float> getData() {
		return data;
	}

}
