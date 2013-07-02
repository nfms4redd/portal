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

import java.io.IOException;

/**
 * Interface that obtains a {@link Location} instance to locate the place where
 * {@link Layer}s have their data
 * 
 * @author fergonco
 */
public interface DataLocator {

	/**
	 * @param layer
	 * @return
	 * @throws IOException
	 *             If there is a problem obtaining the layer location
	 * @throws CannotFindLayerException
	 *             If the layer location cannot be found
	 */
	Location locate(Layer layer) throws IOException, CannotFindLayerException;

}
