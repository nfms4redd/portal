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
package org.fao.unredd.statsCalculator;

/**
 * Signals that two rasters of a set that were supposed to have the same
 * georeferencing information and raster size (width, height, cell size, origin,
 * etc.) does not satisfy this condition
 * 
 * @author fergonco
 */
public class MixedRasterGeometryException extends Exception {
	private static final long serialVersionUID = 1L;

	public MixedRasterGeometryException(String message) {
		super(message);
	}

}
