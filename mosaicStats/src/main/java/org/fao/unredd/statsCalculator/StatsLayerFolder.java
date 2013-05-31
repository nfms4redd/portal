package org.fao.unredd.statsCalculator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXB;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fao.unredd.layers.NoSuchGeoserverLayerException;
import org.fao.unredd.statsCalculator.generated.VariableType;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverage.grid.io.UnknownFormat;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.raster.AreaGridProcess;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;
import com.vividsolutions.jts.geom.Envelope;

/**
 * <p>
 * Calculates the statistics of a GeoServer temporal mosaic. Receives as input
 * the folder where the tiff files are.
 * </p>
 * <p>
 * The contents of the folder that are relevant for this process are:
 * </p>
 * <ul>
 * <li>the tiff files of the mosaic. They contain two values: 0 for non forest,
 * 1 for forest</li>
 * <li>an indexer.properties file indicating how to obtain the timestamp
 * associated to each file.</li>
 * </ul>
 * 
 * @author fergonco
 */
public class StatsLayerFolder extends AbstractLayerFolder {

	private static final String SAMPLE_AREAS_FILE_NAME = "sample-areas.tiff";
	public static final String SAMPLE_AREAS_RELATIVE_PATH = "work/"
			+ SAMPLE_AREAS_FILE_NAME;

	private ZonalStatistics statisticsConfiguration;

	/**
	 * Builds a new StatsCalculator instance
	 * 
	 * @param folder
	 * @throws IllegalArgumentException
	 *             If the folder does not exist
	 * @throws InvalidFolderStructureException
	 *             If the layer does not follow the expected rules
	 */
	public StatsLayerFolder(File folder) throws IllegalArgumentException,
			InvalidFolderStructureException {
		super(folder);
		if (!getDataFolder().exists()) {
			throw new InvalidFolderStructureException(
					"The folder does not contain a subfolder "
							+ "'mosaic' containing the coverages: "
							+ folder.getAbsolutePath(), getDataFolder());
		}
		if (!getConfigurationFolder().exists()) {
			throw new InvalidFolderStructureException(
					"The folder does not contain a subfolder "
							+ "'configuration': " + folder.getAbsolutePath(),
					getConfigurationFolder());
		}
		// Get the configuration of the zonal-stats
		File zonalStatisticsFile = new File(getConfigurationFolder(),
				"zonal-statistics.xml");
		if (!zonalStatisticsFile.exists()) {
			throw new InvalidFolderStructureException(
					"The folder does not contain the configuration for the statistics",
					zonalStatisticsFile);
		}
		this.statisticsConfiguration = JAXB.unmarshal(zonalStatisticsFile,
				ZonalStatistics.class);
	}

	public static void main(String[] args) throws IllegalArgumentException,
			IOException {
		Options options = new Options();
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Folder with the temporal mosaic");
		Option option = OptionBuilder.create("f");
		options.addOption(option);
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
			printUsage();
			System.exit(-1);
		}

		// Read folder as unique parameter
		File folder = null;
		if (cmd.hasOption("f")) {
			folder = new File(cmd.getOptionValue("f"));
		} else {
			printUsage();
			System.exit(-1);
		}
		try {
			StatsLayerFolder statsCalculator = new StatsLayerFolder(folder);
			statsCalculator.run(new CalculationListener() {

				@Override
				public void calculate(File areaRaster, File mask,
						File classificationLayer, String classificationFieldName) {
					System.out.println("Executing the stats for ("
							+ mask.getAbsolutePath() + ") with "
							+ classificationLayer + "/"
							+ classificationFieldName + " as classification ");
				}
			}, null);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	public void run(CalculationListener calculationListener,
			GeoserverLayerFolderTranslator geoserverLayerFolderTranslator)
			throws IllegalArgumentException, IOException,
			MixedRasterGeometryException, InvalidFolderStructureException,
			ConfigurationException {
		// For each zonal statistics
		for (VariableType variable : this.statisticsConfiguration.getVariable()) {
			MosaicLayerFolder mosaicLayer;
			try {
				mosaicLayer = new MosaicLayerFolder(
						geoserverLayerFolderTranslator.getLayerFolder(variable
								.getLayer()));
			} catch (NoSuchGeoserverLayerException e) {
				throw new ConfigurationException(
						"The layer specified in the configuration cannot be found in the geoserver instance: "
								+ variable.getLayer());
			}
			// Get a hashmap with the association between timestamps and files
			TreeMap<Date, File> files = mosaicLayer.getTimestamps();

			// Obtain the raster info from first tiff
			Entry<Date, File> firstSnapshot = files.firstEntry();
			File firstSnapshotFile = firstSnapshot.getValue();
			RasterInfo firstSnapshotInfo = new RasterInfo(firstSnapshotFile);

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

				/*
				 * Remove the area-raster if it does not match the snapshots
				 * geometry
				 */
				File areaRaster = getSampleAreasRasterFile(mosaicLayer);
				if (areaRaster.exists()) {
					if (!firstSnapshotInfo.matchesGeometry(new RasterInfo(
							areaRaster))) {
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
					GeneralEnvelope generalEnvelope = firstSnapshotInfo
							.getEnvelope();
					Envelope jtsEnvelope = new Envelope(
							generalEnvelope.getMinimum(0),
							generalEnvelope.getMaximum(0),
							generalEnvelope.getMinimum(1),
							generalEnvelope.getMaximum(1));
					ReferencedEnvelope envelope = new ReferencedEnvelope(
							jtsEnvelope, firstSnapshotInfo.getCRS());
					int width = firstSnapshotInfo.getWidth();
					int height = firstSnapshotInfo.getHeight();
					GridCoverage2D grid = process.execute(envelope, width,
							height);
					GeoTiffWriter writer = null;
					try {
						writer = new GeoTiffWriter(areaRaster);
					} catch (IOException e) {
						// TODO
						System.err
								.println("Cannot create the writer for file: "
										+ areaRaster.getAbsolutePath());
						System.exit(-3);
					}
					try {
						GeoTiffWriteParams params = new GeoTiffWriteParams();
						params.setTilingMode(TIFFImageWriteParam.MODE_EXPLICIT);
						params.setTiling(512, 512);
						ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS
								.createValue();
						value.setValue(params);
						try {
							writer.write(grid,
									new GeneralParameterValue[] { value });
						} catch (NullPointerException e) {
							/**
							 * if no permission, GT gives NPE, we have to report
							 * this to fix it and handle properly the exception.
							 * {@link TriggerTest#testGTGeotiffWriterExceptionManagement()}
							 */
							throw new IOException(
									"Error writing the area raster", e);
						}
					} catch (RuntimeException e) {
						throw new RuntimeException(
								"Bug writing the area raster", e);
					} catch (IOException e) {
						throw new IOException("Error writing the area raster",
								e);
					} finally {
						writer.dispose();
					}
				}

				calculationListener.calculate(areaRaster, timestampFile,
						getShapefile(),
						this.statisticsConfiguration.getZoneIdField());
			}
		}

	}

	public File getSampleAreasRasterFile(MosaicLayerFolder mosaicLayer) {
		return mosaicLayer.getWorkFile(SAMPLE_AREAS_FILE_NAME);
	}

	private File getShapefile() {
		return getDataFolder().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".shp");
			}
		})[0];
	}

	private static void printUsage() {
		throw new UnsupportedOperationException("Print usage not supported yet");
	}

	private static class RasterInfo {

		private GeneralEnvelope envelope;
		private GridEnvelope gridRange;
		private CoordinateReferenceSystem crs;

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
}
