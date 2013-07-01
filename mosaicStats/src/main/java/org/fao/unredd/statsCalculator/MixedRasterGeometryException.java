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