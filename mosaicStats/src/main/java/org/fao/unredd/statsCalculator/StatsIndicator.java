package org.fao.unredd.statsCalculator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.bind.JAXB;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.MosaicLayer;
import org.fao.unredd.layers.NoSuchConfigurationException;
import org.fao.unredd.layers.NoSuchGeoserverLayerException;
import org.fao.unredd.layers.folder.LayerFolderImpl;
import org.fao.unredd.process.ProcessExecutionException;
import org.fao.unredd.statsCalculator.generated.VariableType;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;

/**
 * Calculates the statistics on a GeoServer layer. Receives as input the layer
 * instance.
 * 
 * @author fergonco
 */
public class StatsIndicator {

	private Layer layer;
	private LayerFactory layerFactory;

	public StatsIndicator(LayerFactory layerFactory, Layer layer)
			throws NoSuchGeoserverLayerException {
		if (!layer.getDataFolder().exists()) {
			throw new IllegalArgumentException(
					"The layer data folder does not exist");
		}
		this.layerFactory = layerFactory;
		this.layer = layer;
	}

	private ZonalStatistics readConfiguration() throws IOException,
			ConfigurationException {
		String statisticsConfigurationContent;
		try {
			statisticsConfigurationContent = layer
					.getConfiguration("zonal-statistics.xml");
		} catch (NoSuchConfigurationException e) {
			throw new ConfigurationException(
					"The layer does not have the configuration to run the statistics indicator",
					e);
		}
		ZonalStatistics statisticsConfiguration = JAXB.unmarshal(
				new ByteArrayInputStream(statisticsConfigurationContent
						.getBytes()), ZonalStatistics.class);
		return statisticsConfiguration;
	}

	private Iterable<MosaicLayer> getMosaicLayers(
			ZonalStatistics statisticsConfiguration)
			throws ConfigurationException {
		ArrayList<MosaicLayer> ret = new ArrayList<MosaicLayer>();
		for (VariableType variable : statisticsConfiguration.getVariable()) {
			try {
				MosaicLayer mosaicLayer = layerFactory.newMosaicLayer(variable
						.getLayer());
				ret.add(mosaicLayer);
			} catch (NoSuchGeoserverLayerException e) {
				throw new ConfigurationException(
						"The layer specified in the configuration cannot be found in the geoserver instance: "
								+ variable.getLayer(), e);
			}
		}

		return ret;
	}

	/**
	 * Executes the indicator on the associated layer. Analyzes the layer to
	 * validate the contents prior to the execution of the indicator
	 * 
	 * @throws ConfigurationException
	 *             If the configuration of the zonal statistics is wrong
	 * @throws MixedRasterGeometryException
	 *             If the rasters of one of the referenced mosaic layer don't
	 *             have homogeneous geometry
	 * @throws IOException
	 *             If a general IO error takes place during the calculation
	 * @throws ProcessExecutionException
	 */
	public void run() throws ConfigurationException,
			MixedRasterGeometryException, IOException,
			ProcessExecutionException {
		ZonalStatistics configuration = readConfiguration();
		SimpleDateFormat timeFormat = getTimeFormat(configuration);
		Iterable<MosaicLayer> mosaicLayers = getMosaicLayers(configuration);
		File dataFolder = layer.getDataFolder();
		File shapefile = dataFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".shp");
			}
		})[0];
		for (MosaicLayer mosaicLayer : mosaicLayers) {
			OutputBuilder outputBuilder = new OutputBuilder(layer,
					configuration);
			MosaicProcessor processor = new MosaicProcessor(outputBuilder,
					mosaicLayer);
			processor.process(shapefile, timeFormat, configuration);
			outputBuilder.writeResult();
		}
	}

	private SimpleDateFormat getTimeFormat(ZonalStatistics configuration)
			throws ConfigurationException {
		String dateFormat = configuration.getPresentationData().getDateFormat();
		if (dateFormat != null) {
			try {
				return new SimpleDateFormat(dateFormat);
			} catch (IllegalArgumentException e) {
				throw new ConfigurationException(
						"The date format of the configuration is not valid: "
								+ dateFormat, e);
			}
		} else {
			return new SimpleDateFormat();
		}
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
			StatsIndicator statsIndicator = new StatsIndicator(null,
					new LayerFolderImpl(folder));
			statsIndicator.run();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	private static void printUsage() {
		throw new UnsupportedOperationException("Print usage not supported yet");
	}
}
