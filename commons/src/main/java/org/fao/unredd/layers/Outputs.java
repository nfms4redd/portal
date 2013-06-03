package org.fao.unredd.layers;

import java.util.ArrayList;
import java.util.Iterator;

public class Outputs extends ArrayList<Output> {
	private static final long serialVersionUID = 1L;

	public Outputs(Output... indicators) {
		for (Output indicator : indicators) {
			add(indicator);
		}
	}

	public String toJSON() {
		StringBuilder ret = new StringBuilder("[");
		String separator = "";
		Iterator<Output> it = iterator();
		while (it.hasNext()) {
			Output indicator = it.next();
			ret.append(separator).append(indicator.toJSON());
			separator = ",";
		}

		return ret.append("]").toString();
	}

}
