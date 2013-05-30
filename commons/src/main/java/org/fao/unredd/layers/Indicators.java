package org.fao.unredd.layers;

import java.util.ArrayList;
import java.util.Iterator;

public class Indicators extends ArrayList<Indicator> {
	private static final long serialVersionUID = 1L;

	public Indicators(Indicator... indicators) {
		for (Indicator indicator : indicators) {
			add(indicator);
		}
	}

	public String toJSON() {
		StringBuilder ret = new StringBuilder("[");
		String separator = "";
		Iterator<Indicator> it = iterator();
		while (it.hasNext()) {
			Indicator indicator = it.next();
			ret.append(separator).append(indicator.toJSON());
			separator = ",";
		}

		return ret.append("]").toString();
	}

}
