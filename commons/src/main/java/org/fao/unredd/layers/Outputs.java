package org.fao.unredd.layers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A collection of {@link OutputDescriptor}s
 * 
 * @author fergonco
 */
public class Outputs extends ArrayList<OutputDescriptor> {
	private static final long serialVersionUID = 1L;

	public Outputs(ArrayList<OutputDescriptor> outputDescriptors) {
		this.addAll(outputDescriptors);
	}

	public Outputs(OutputDescriptor... outputDescriptors) {
		Collections.addAll(this, outputDescriptors);
	}

	public String toJSON() {
		StringBuilder ret = new StringBuilder("[");
		String separator = "";
		Iterator<OutputDescriptor> it = iterator();
		while (it.hasNext()) {
			OutputDescriptor outputDescriptor = it.next();
			ret.append(separator).append(outputDescriptor.toJSON());
			separator = ",";
		}

		return ret.append("]").toString();
	}

}
