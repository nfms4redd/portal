package org.fao.unredd.statsCalculator;

import java.io.File;
import java.io.IOException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.raster.AreaGridProcess;
import org.geotools.referencing.CRS;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;

/**
 * Class that manages the area raster created to hold the areas of the pixels of
 * an hypothetical raster whose georeferencing information is passed as
 * parameter in the constructor
 * 
 * @author fergonco
 */
public class AreaRasterManager {

	private static CoordinateReferenceSystem epsg4326;

	static {
		try {
			epsg4326 = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			throw new RuntimeException("bug", e);
		} catch (FactoryException e) {
			throw new RuntimeException("bug", e);
		}
	}

	private RasterInfo referenceRasterInfo;
	private File areaRaster;

	/**
	 * Builds a new manager specifying the file of the raster to build and the
	 * georeferencing information used as reference
	 * 
	 * @param areaRaster
	 * @param referenceRasterInfo
	 * @throws IllegalArgumentException
	 *             If the reference raster information contains a CRS that is
	 *             not in EPSG:4326
	 */
	public AreaRasterManager(File areaRaster, RasterInfo referenceRasterInfo)
			throws IllegalArgumentException {
		if (!referenceRasterInfo.getCRS().equals(epsg4326)) {
			throw new IllegalArgumentException(
					"Only raster in EPSG:4326 can be processed");
		}
		this.areaRaster = areaRaster;
		this.referenceRasterInfo = referenceRasterInfo;

	}

	/**
	 * Ensures the raster file pointed by {@link #areaRaster} has the same size
	 * and is georeferenced in the same place as {@link #referenceRasterInfo}.
	 * This method will check if there is already a raster with the requirements
	 * and if it does not exist or the raster information does not match it will
	 * create a new raster
	 * 
	 * @throws IOException
	 */
	public void createCompatibleAreaRaster() throws IOException {
		/*
		 * Remove the area-raster if it does not match the snapshots geometry
		 */
		if (areaRaster.exists()) {
			if (!referenceRasterInfo
					.matchesGeometry(new RasterInfo(areaRaster))) {
				if (!areaRaster.delete()) {
					throw new IOException("The area raster geometry "
							+ "does not match "
							+ "the snapshots' and could not "
							+ "be deleted to be regenerated");
				}
			}
		}

		// Generate the area-raster if it does not exist
		if (!areaRaster.exists()) {
			File workFolder = areaRaster.getParentFile();
			if (!workFolder.exists() && !workFolder.mkdir()) {
				throw new IOException("Cannot create work folder: "
						+ workFolder);
			}
			AreaGridProcess process = new AreaGridProcess();
			GeneralEnvelope generalEnvelope = referenceRasterInfo.getEnvelope();
			// In EPSG:4326 we have first lat and then lon
			ReferencedEnvelope envelope = new ReferencedEnvelope(
					generalEnvelope.getMinimum(1),
					generalEnvelope.getMaximum(1),
					generalEnvelope.getMinimum(0),
					generalEnvelope.getMaximum(0), referenceRasterInfo.getCRS());
			int width = referenceRasterInfo.getWidth();
			int height = referenceRasterInfo.getHeight();
			GridCoverage2D grid = process.execute(envelope, width, height);
			GeoTiffWriter writer = new GeoTiffWriter(areaRaster);
			try {
				GeoTiffWriteParams params = new GeoTiffWriteParams();
				params.setTilingMode(TIFFImageWriteParam.MODE_EXPLICIT);
				params.setTiling(512, 512);
				ParameterValue<GeoToolsWriteParams> gtParams = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS
						.createValue();
				gtParams.setValue(params);
				ParameterValue<Boolean> retainAxisOrderParam = GeoTiffFormat.RETAIN_AXES_ORDER
						.createValue();
				retainAxisOrderParam.setValue(false);
				try {
					writer.write(grid, new GeneralParameterValue[] { gtParams,
							retainAxisOrderParam });
				} catch (NullPointerException e) {
					/**
					 * if no permission, GT gives NPE, we have to report this to
					 * fix it and handle properly the exception.
					 * {@link TriggerTest#testGTGeotiffWriterExceptionManagement()}
					 */
					throw new IOException("Error writing the area raster", e);
				}
			} catch (RuntimeException e) {
				throw new RuntimeException("Bug writing the area raster", e);
			} catch (IOException e) {
				throw new IOException("Error writing the area raster", e);
			} finally {
				writer.dispose();
			}
		}
	}

}
