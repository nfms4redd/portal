package org.fao.unredd.statsCalculator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.fao.unredd.layers.MosaicLayer;
import org.fao.unredd.process.ProcessExecutionException;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;

public class MosaicProcessor {

	private MosaicLayer mosaicLayer;
	private OutputBuilder outputBuilder;

	public MosaicProcessor(OutputBuilder outputBuilder, MosaicLayer mosaicLayer) {
		this.outputBuilder = outputBuilder;
		this.mosaicLayer = mosaicLayer;
	}

	public void process(File shapefile, SimpleDateFormat timeFormat,
			ZonalStatistics statisticsConfiguration) throws IOException,
			MixedRasterGeometryException, ProcessExecutionException {

		// Get a hashmap with the association between timestamps and files
		TreeMap<Date, File> files = mosaicLayer.getTimestamps();

		// Obtain the raster info from first tiff
		Entry<Date, File> firstSnapshot = files.firstEntry();
		File firstSnapshotFile = firstSnapshot.getValue();
		RasterInfo firstSnapshotInfo = new RasterInfo(firstSnapshotFile);
		File areaRaster = mosaicLayer
				.getWorkFile(StatsIndicatorConstants.SAMPLE_AREAS_FILE_NAME);
		AreaRasterManager areaRasterManager = new AreaRasterManager(areaRaster,
				firstSnapshotInfo);

		// Calculate statistics for every snapshot
		Iterator<Date> timestampIterator = files.keySet().iterator();
		while (timestampIterator.hasNext()) {
			Date timestamp = timestampIterator.next();
			File timestampFile = files.get(timestamp);

			// Check the snapshot matches first snapshot's geometry
			if (!new RasterInfo(timestampFile)
					.matchesGeometry(firstSnapshotInfo)) {
				throw new MixedRasterGeometryException("The snapshot of '"
						+ timestamp + "' does not match the "
						+ "geometry of the first snapshot: '"
						+ firstSnapshot.getKey() + "'");
			}

			areaRasterManager.createCompatibleAreaRaster();

			outputBuilder.addToOutput(areaRaster, timeFormat.format(timestamp),
					timestampFile, shapefile, firstSnapshotInfo.getWidth(),
					firstSnapshotInfo.getHeight());

		}
	}

}
