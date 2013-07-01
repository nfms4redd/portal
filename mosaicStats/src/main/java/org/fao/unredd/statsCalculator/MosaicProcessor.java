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
import org.fao.unredd.layers.PasswordGetter;
import org.fao.unredd.layers.folder.InvalidFolderStructureException;
import org.fao.unredd.process.ProcessExecutionException;

/**
 * Class to perform the calculation of statistics process
 * 
 * @author fergonco
 */
public class MosaicProcessor {

	private DataLocator dataLocator;
	private MosaicLayer mosaicLayer;
	private OutputBuilder outputBuilder;

	/**
	 * Builds a new instance
	 * 
	 * @param dataLocator
	 *            instance necessary to resolve the location of the layers
	 * @param outputBuilder
	 *            instance necessary to build the output expected by the portal
	 * @param mosaicLayer
	 *            Mosaic to process
	 */
	public MosaicProcessor(DataLocator dataLocator,
			OutputBuilder outputBuilder, MosaicLayer mosaicLayer) {
		this.dataLocator = dataLocator;
		this.outputBuilder = outputBuilder;
		this.mosaicLayer = mosaicLayer;
	}

	/**
	 * Main loop to calculate the statistics for a {@link MosaicLayer}. Iterates
	 * through each of the timestamps of the mosaic layer, checks the rasters
	 * are referenced in the same spot, the area raster exists and is also
	 * referenced in the same spot and delegates the building of the output to
	 * {@link #outputBuilder}
	 * 
	 * Processes the specified location containing the objects for which the
	 * 
	 * @param zonesLocation
	 * @param passwordGetter
	 * @throws IOException
	 *             If there is any problem accessing the different files used in
	 *             the calculation
	 * @throws MixedRasterGeometryException
	 *             If the timestamps in the layer mosaic have a different
	 *             structure or are georeferenced in different places
	 * @throws ProcessExecutionException
	 *             If any native process could not be executed properly
	 * @throws InvalidFolderStructureException
	 *             If the mosaic layer has not the expected structure
	 * @throws CannotFindLayerException
	 *             If the {@link #dataLocator} cannot resolve any of the
	 *             involved layers
	 */
	public void process(Location zonesLocation, PasswordGetter passwordGetter)
			throws IOException, MixedRasterGeometryException,
			ProcessExecutionException, InvalidFolderStructureException,
			CannotFindLayerException {

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
					firstSnapshotInfo.getHeight(), passwordGetter);
		}
	}

}
