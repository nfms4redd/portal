package org.fao.unredd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import org.fao.unredd.statsCalculator.AreaRasterManager;
import org.fao.unredd.statsCalculator.RasterInfo;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

public class AreaSampleManagerTest {

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
