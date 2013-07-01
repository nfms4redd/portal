package org.fao.unredd.statsCalculator;

import java.io.File;
import java.io.IOException;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverage.grid.io.UnknownFormat;
import org.geotools.geometry.GeneralEnvelope;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Gathers information about a raster
 * 
 * @author fergonco
 */
public class RasterInfo {

	private GeneralEnvelope envelope;
	private GridEnvelope gridRange;
	private CoordinateReferenceSystem crs;

	/**
	 * Creates a new instance. Reads the raster and keeps its georeferencing and
	 * size information
	 * 
	 * @param raster
	 * @throws IOException
	 */
	public RasterInfo(File raster) throws IOException {
		AbstractGridFormat format = GridFormatFinder.findFormat(raster);
		if (format instanceof UnknownFormat) {
			throw new IOException("Unable to read the TIFF file: "
					+ raster.getAbsolutePath());
		}
		AbstractGridCoverage2DReader reader = format.getReader(raster);
		if (reader == null) {
			throw new IOException("Unable to read the TIFF file: "
					+ raster.getAbsolutePath());
		}
		envelope = reader.getOriginalEnvelope();
		gridRange = reader.getOriginalGridRange();
		crs = reader.getCrs();
		reader.dispose();
	}

	public int getHeight() {
		return gridRange.getSpan(1);
	}

	public int getWidth() {
		return gridRange.getSpan(0);
	}

	public GeneralEnvelope getEnvelope() {
		return envelope;
	}

	public CoordinateReferenceSystem getCRS() {
		return crs;
	}

	public boolean matchesGeometry(RasterInfo that) {
		return this.getCRS().toWKT().equals(that.getCRS().toWKT())
				&& this.getEnvelope().equals(that.getEnvelope(), 0.000001,
						false)//
				&& this.getWidth() == that.getWidth()
				&& this.getHeight() == that.getHeight();
	}
}