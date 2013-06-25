package org.fao.unredd.statsCalculator;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.fao.unredd.layers.CannotFindLayerException;
import org.fao.unredd.layers.DataLocator;
import org.fao.unredd.layers.Location;
import org.fao.unredd.layers.MosaicLayer;
import org.fao.unredd.layers.folder.InvalidFolderStructureException;
import org.fao.unredd.process.ProcessExecutionException;

public class MosaicProcessor {

	private DataLocator dataLocator;
	private MosaicLayer mosaicLayer;
	private OutputBuilder outputBuilder;

	public MosaicProcessor(DataLocator dataLocator,
			OutputBuilder outputBuilder, MosaicLayer mosaicLayer) {
		this.dataLocator = dataLocator;
		this.outputBuilder = outputBuilder;
		this.mosaicLayer = mosaicLayer;
	}

	public void process(Location zonesLocation) throws IOException,
			MixedRasterGeometryException, ProcessExecutionException,
			InvalidFolderStructureException, CannotFindLayerException {

		// Get a hashmap with the association between timestamps and files
		TreeMap<Date, File> files = mosaicLayer.getTimestamps(dataLocator
				.locate(mosaicLayer));

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

			outputBuilder.addToOutput(areaRaster, timestamp, timestampFile,
					zonesLocation, firstSnapshotInfo.getWidth(),
					firstSnapshotInfo.getHeight());
		}
	}

}
