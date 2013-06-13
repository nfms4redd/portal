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
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;
import com.vividsolutions.jts.geom.Envelope;

public class AreaRasterManager {

	private RasterInfo referenceRasterInfo;
	private File areaRaster;

	public AreaRasterManager(File areaRaster, RasterInfo referenceRasterInfo) {
		this.areaRaster = areaRaster;
		this.referenceRasterInfo = referenceRasterInfo;
	}

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
			Envelope jtsEnvelope = new Envelope(generalEnvelope.getMinimum(0),
					generalEnvelope.getMaximum(0),
					generalEnvelope.getMinimum(1),
					generalEnvelope.getMaximum(1));
			ReferencedEnvelope envelope = new ReferencedEnvelope(jtsEnvelope,
					referenceRasterInfo.getCRS());
			int width = referenceRasterInfo.getWidth();
			int height = referenceRasterInfo.getHeight();
			GridCoverage2D grid = process.execute(envelope, width, height);
			GeoTiffWriter writer = new GeoTiffWriter(areaRaster);
			try {
				GeoTiffWriteParams params = new GeoTiffWriteParams();
				params.setTilingMode(TIFFImageWriteParam.MODE_EXPLICIT);
				params.setTiling(512, 512);
				ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS
						.createValue();
				value.setValue(params);
				try {
					writer.write(grid, new GeneralParameterValue[] { value });
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
