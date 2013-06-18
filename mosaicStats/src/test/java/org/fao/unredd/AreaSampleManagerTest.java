package org.fao.unredd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import org.fao.unredd.statsCalculator.AreaRasterManager;
import org.fao.unredd.statsCalculator.RasterInfo;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

public class AreaSampleManagerTest {

	@Test
	public void testAreaRasterResult() throws Exception {
		File referenceRaster = new File("src/test/resources/popmask1990.tiff");
		RasterInfo referenceRasterInfo = new RasterInfo(referenceRaster);
		File outputRaster = File
				.createTempFile("testAreaRasterResult", ".tiff");
		assertTrue(outputRaster.delete());
		AreaRasterManager areaRasterManager = new AreaRasterManager(
				outputRaster, referenceRasterInfo);
		areaRasterManager.createCompatibleAreaRaster();

		RasterInfo outputRasterInfo = new RasterInfo(outputRaster);
		assertTrue(outputRasterInfo.getEnvelope().contains(
				referenceRasterInfo.getEnvelope(), true));
		AbstractGridFormat format = GridFormatFinder.findFormat(outputRaster);
		AbstractGridCoverage2DReader reader = format.getReader(outputRaster);
		GridCoverage2D gc = reader.read(new GeneralParameterValue[0]);
		double[] samples = new double[1];
		double minlat = gc.getEnvelope().getMinimum(1) + 0.1;
		double maxlat = gc.getEnvelope().getMaximum(1) - 0.1;
		double minlon = gc.getEnvelope().getMinimum(0) + 0.1;
		double maxlon = gc.getEnvelope().getMaximum(0) - 0.1;
		try {
			double minLatMinLon = gc.evaluate(
					new Point2D.Double(minlon, minlat), samples)[0];
			double minLatMaxLon = gc.evaluate(
					new Point2D.Double(maxlon, minlat), samples)[0];
			double maxLatMinLon = gc.evaluate(
					new Point2D.Double(minlon, maxlat), samples)[0];
			double maxLatMaxLon = gc.evaluate(
					new Point2D.Double(maxlon, maxlat), samples)[0];
			/*
			 * No differences if latitude is constant
			 */
			assertEquals(minLatMinLon, minLatMaxLon, 0.0001);
			assertEquals(maxLatMinLon, maxLatMaxLon, 0.0001);
			/*
			 * increase as latitude increases
			 */
			assertTrue(minLatMinLon < maxLatMinLon);
			assertTrue(minLatMaxLon < maxLatMaxLon);
		} finally {
			gc.dispose(false);
			reader.dispose();
		}

		outputRaster.delete();
	}

	@Test
	public void testOkExistingBadSampleAreas() throws Exception {
		File areaRaster = new File(
				"src/test/resources/backup-sample-areas.tiff");
		long lastModified = areaRaster.lastModified();

		RasterInfo referenceRasterInfo = mockNonMatchingRasterInfo();
		AreaRasterManager areaRasterManager = new AreaRasterManager(areaRaster,
				referenceRasterInfo);
		areaRasterManager.createCompatibleAreaRaster();

		assertTrue(new RasterInfo(areaRaster)
				.matchesGeometry(referenceRasterInfo));
		assertTrue(areaRaster.lastModified() != lastModified);
	}

	private RasterInfo mockNonMatchingRasterInfo()
			throws NoSuchAuthorityCodeException, FactoryException {
		RasterInfo referenceRasterInfo = mock(RasterInfo.class);
		when(referenceRasterInfo.getWidth()).thenReturn(100);
		when(referenceRasterInfo.getHeight()).thenReturn(50);
		when(referenceRasterInfo.getCRS()).thenReturn(CRS.decode("EPSG:4326"));
		when(referenceRasterInfo.getEnvelope()).thenReturn(
				new GeneralEnvelope(new Rectangle2D.Double(2, 4, 10, 10)));
		when(referenceRasterInfo.matchesGeometry(any(RasterInfo.class)))
				.thenReturn(false);
		return referenceRasterInfo;
	}

	@Test
	public void testOkExistingBadSampleAreasCannotBeDeleted() throws Exception {
		File readOnlyFolder = new File("src/test/resources/readonly");
		readOnlyFolder.setReadOnly();
		File areaRaster = new File(readOnlyFolder, "sample-areas.tiff");
		RasterInfo referenceRasterInfo = mockNonMatchingRasterInfo();
		AreaRasterManager areaRasterManager = new AreaRasterManager(areaRaster,
				referenceRasterInfo);
		try {
			areaRasterManager.createCompatibleAreaRaster();
			fail();
		} catch (IOException e) {
		} finally {
			readOnlyFolder.setWritable(true);
		}
	}

	@Test
	public void testErrorCreatingAreaRaster() throws Exception {
		File readOnlyFolder = new File("src/test/resources/readonly");
		readOnlyFolder.setReadOnly();
		File areaRaster = new File(readOnlyFolder, "new-sample-areas.tiff");
		RasterInfo referenceRasterInfo = mockNonMatchingRasterInfo();
		AreaRasterManager areaRasterManager = new AreaRasterManager(areaRaster,
				referenceRasterInfo);
		try {
			areaRasterManager.createCompatibleAreaRaster();
			fail();
		} catch (IOException e) {
		} finally {
			readOnlyFolder.setWritable(true);
		}
	}

}
