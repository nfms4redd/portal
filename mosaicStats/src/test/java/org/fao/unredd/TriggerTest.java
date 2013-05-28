package org.fao.unredd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.geosolutions.imageio.plugins.tiff.TIFFImageWriteParam;

import java.io.File;

import org.fao.unredd.statsCalculator.StatsLayerFolder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;

public class TriggerTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testGTGeotiffWriterExceptionManagement() throws Exception {
		File file = new File(folder.getRoot(), "test.tiff");
		assertTrue(folder.getRoot().setReadOnly());

		File dummyReadTiff = new File("src/test/resources/"
				+ "temporalMosaic/data/snapshot_2000.tiff");
		AbstractGridFormat format = GridFormatFinder.findFormat(dummyReadTiff);
		AbstractGridCoverage2DReader reader = format.getReader(dummyReadTiff);

		GridCoverage2D coverage = reader.read(null);

		GeoTiffWriteParams params = new GeoTiffWriteParams();
		params.setTilingMode(TIFFImageWriteParam.MODE_EXPLICIT);
		params.setTiling(512, 512);
		ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS
				.createValue();
		value.setValue(params);
		try {
			GeoTiffWriter writer = new GeoTiffWriter(file);
			writer.write(coverage, new GeneralParameterValue[] { value });

			new StatsLayerFolder(file);
			fail("This code will fail when the GT "
					+ "geotiff  writer will produce an IO "
					+ "exception instead of a NPE when "
					+ "trying to write in a read only folder. "
					+ "Then, change the exception management of "
					+ "the code that uses the writer and remove this test");
		} catch (NullPointerException e) {
		}
	}
}
