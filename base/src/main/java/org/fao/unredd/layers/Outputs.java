/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.layers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A collection of {@link OutputDescriptor}s
 * 
 * @author fergonco
 */
public class Outputs extends ArrayList<Output> {
	private static final long serialVersionUID = 1L;

	public Outputs(ArrayList<Output> temp) {
		this.addAll(temp);
	}

	public Outputs(Output... outputs) {
		Collections.addAll(this, outputs);
	}

	public Outputs(OutputDescriptor outputDescriptor) {
		this.add((Output) outputDescriptor);
	}

	public String toJSON() {
		StringBuilder ret = new StringBuilder("[");
		String separator = "";
		Iterator<Output> it = iterator();
		while (it.hasNext()) {
			OutputDescriptor outputDescriptor = it.next();
			ret.append(separator).append(outputDescriptor.toJSON());
			separator = ",";
		}

		return ret.append("]").toString();
	}

}
