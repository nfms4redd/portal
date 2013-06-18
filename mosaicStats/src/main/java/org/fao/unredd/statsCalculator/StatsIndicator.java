package org.fao.unredd.statsCalculator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.MosaicLayer;
import org.fao.unredd.layers.NoSuchConfigurationException;
import org.fao.unredd.layers.NoSuchLayerException;
import org.fao.unredd.layers.folder.FolderLayerFactory;
import org.fao.unredd.layers.folder.InvalidFolderStructureException;
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

	private static final String LAYERNAME_PARAM_NAME = "l";
	private static final String ROOT_PARAM_NAME = "r";
	private Layer layer;
	private LayerFactory layerFactory;

	public StatsIndicator(LayerFactory layerFactory, Layer layer)
			throws NoSuchLayerException {
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
		File dataFolder = layer.getDataFolder();
		File shapefile = dataFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".shp");
			}
		})[0];
		for (VariableType variable : configuration.getVariable()) {
			MosaicLayer mosaicLayer;
			try {
				mosaicLayer = layerFactory.newMosaicLayer(variable.getLayer());
			} catch (NoSuchLayerException e) {
				throw new ConfigurationException(
						"The layer specified in the configuration cannot be found: "
								+ variable.getLayer(), e);
			} catch (InvalidFolderStructureException e) {
				throw new ConfigurationException(
						"The layer specified in the configuration is not a mosaic: "
								+ variable.getLayer(), e);
			}
			OutputBuilder outputBuilder = new OutputBuilder(layer, variable);
			MosaicProcessor processor = new MosaicProcessor(outputBuilder,
					mosaicLayer);
			processor.process(shapefile);
			outputBuilder.writeResult(variable.getLayer());
		}
	}

	public static void main(String[] args) throws IllegalArgumentException,
			IOException {
		Options options = new Options();
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("Name of the layer to use for the calculation of the stats indicators");
		options.addOption(OptionBuilder.create(LAYERNAME_PARAM_NAME));
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Root of the layer folder structure");
		options.addOption(OptionBuilder.create(ROOT_PARAM_NAME));
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		String cmdLineSyntax = "stats-indicator.sh -l <layer-name> -r <root-folder>";
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
			new HelpFormatter().printHelp(cmdLineSyntax, options);
			System.exit(-1);
		}
		if (!cmd.hasOption(LAYERNAME_PARAM_NAME)
				|| !cmd.hasOption(ROOT_PARAM_NAME)) {
			new HelpFormatter().printHelp(cmdLineSyntax, options);
			System.exit(-1);
		}

		String layerName = cmd.getOptionValue(LAYERNAME_PARAM_NAME);
		File rootFolder = new File(cmd.getOptionValue(ROOT_PARAM_NAME));

		InputStream log4jStream = StatsIndicator.class
				.getResourceAsStream("/log4j.properties");
		String statsIndicatorHome = System.getenv("STATS_INDICATOR_HOME");
		System.out.println("STATS_INDICATOR_HOME_PROP=" + statsIndicatorHome);
		if (statsIndicatorHome == null
				|| statsIndicatorHome.trim().length() == 0) {
			statsIndicatorHome = ".";
		}
		System.setProperty("app.root", statsIndicatorHome);
		PropertyConfigurator.configure(log4jStream);
		log4jStream.close();

		try {
			LayerFactory layerFactory = new FolderLayerFactory(rootFolder);
			Layer layer = layerFactory.newLayer(layerName);
			StatsIndicator statsIndicator = new StatsIndicator(layerFactory,
					layer);
			statsIndicator.run();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(getExceptionChainMessage(e));
			System.exit(-1);
		}
	}

	private static List<String> getExceptionChainMessage(Throwable throwable) {
		List<String> result = new ArrayList<String>();
		while (throwable != null) {
			result.add(throwable.getMessage());
			throwable = throwable.getCause();
		}
		return result;
	}
}
