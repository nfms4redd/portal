/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.statsCalculator;

import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.fao.unredd.layers.CannotFindLayerException;
import org.fao.unredd.layers.DataLocator;
import org.fao.unredd.layers.GeoserverDataLocator;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.Location;
import org.fao.unredd.layers.MosaicLayer;
import org.fao.unredd.layers.NoSuchConfigurationException;
import org.fao.unredd.layers.PasswordGetter;
import org.fao.unredd.layers.PasswordGetterFactory;
import org.fao.unredd.layers.folder.FolderLayerFactory;
import org.fao.unredd.layers.folder.InvalidFolderStructureException;
import org.fao.unredd.process.ProcessExecutionException;
import org.fao.unredd.statsCalculator.generated.VariableType;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;

/**
 * Calculates the statistics on a layer.
 * 
 * @author fergonco
 */
public class StatsIndicator {

	private static final String LAYERNAME_PARAM_NAME = "l";
	private static final String LAYERNAME_PARAM_LONG_NAME = "layer";
	private static final String CONF_PARAM_NAME = "c";
	private static final String CONF_PARAM_LONG_NAME = "conf";
	private static final String GS_DATA_PARAM_NAME = "gs";
	private static final String GS_DATA_PARAM_LONG_NAME = "gsdata";
	private Layer layer;
	private LayerFactory layerFactory;
	private DataLocator dataLocator;

	public StatsIndicator(DataLocator dataLocator, LayerFactory layerFactory,
			Layer layer) {
		this.dataLocator = dataLocator;
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
	 * @param passwordGetter
	 * 
	 * @throws ConfigurationException
	 *             If the configuration of the zonal statistics is wrong
	 * @throws MixedRasterGeometryException
	 *             If the rasters of one of the referenced mosaic layer don't
	 *             have homogeneous geometry
	 * @throws IOException
	 *             If a general IO error takes place during the calculation
	 * @throws ProcessExecutionException
	 * @throws InvalidFolderStructureException
	 * @throws NoSuchLayerException
	 * @throws CannotFindLayerException
	 */
	public void run(PasswordGetter passwordGetter)
			throws ConfigurationException, MixedRasterGeometryException,
			IOException, ProcessExecutionException,
			InvalidFolderStructureException, CannotFindLayerException {
		ZonalStatistics configuration = readConfiguration();
		for (VariableType variable : configuration.getVariable()) {
			MosaicLayer mosaicLayer = layerFactory.newMosaicLayer(variable
					.getLayer());
			OutputBuilder outputBuilder = new OutputBuilder(layer, variable);
			MosaicProcessor processor = new MosaicProcessor(dataLocator,
					outputBuilder, mosaicLayer);
			Location location = dataLocator.locate(layer);
			processor.process(location, passwordGetter);
			outputBuilder.writeResult(variable.getLayer());
		}
	}

	public static void main(String[] args) throws IllegalArgumentException,
			IOException {
		Options options = new Options();
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("Name of the layer to use for the calculation of the stats indicators");
		OptionBuilder.withLongOpt(LAYERNAME_PARAM_LONG_NAME);
		options.addOption(OptionBuilder.create(LAYERNAME_PARAM_NAME));
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("Root of the layer configuration folder structure");
		OptionBuilder.withLongOpt(CONF_PARAM_LONG_NAME);
		options.addOption(OptionBuilder.create(CONF_PARAM_NAME));
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("GeoServer data folder");
		OptionBuilder.withLongOpt(GS_DATA_PARAM_LONG_NAME);
		options.addOption(OptionBuilder.create(GS_DATA_PARAM_NAME));
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		String cmdLineSyntax = "stats-indicator.sh -layer <layer-name> -conf <configuration-folder> -gsdata <geoserver-data-dir>";
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
			new HelpFormatter().printHelp(cmdLineSyntax, options);
			System.exit(-1);
		}
		if (!cmd.hasOption(LAYERNAME_PARAM_NAME)
				|| !cmd.hasOption(CONF_PARAM_NAME)
				|| !cmd.hasOption(GS_DATA_PARAM_NAME)) {
			new HelpFormatter().printHelp(cmdLineSyntax, options);
			System.exit(-1);
		}

		String layerName = cmd.getOptionValue(LAYERNAME_PARAM_NAME);
		File rootFolder = new File(cmd.getOptionValue(CONF_PARAM_NAME));
		File gsDataFolder = new File(cmd.getOptionValue(GS_DATA_PARAM_NAME));

		InputStream log4jStream = StatsIndicator.class
				.getResourceAsStream("/log4j.properties");
		String statsIndicatorHome = System.getenv("STATS_INDICATOR_HOME");
		if (statsIndicatorHome == null
				|| statsIndicatorHome.trim().length() == 0) {
			statsIndicatorHome = ".";
		}
		System.setProperty("app.root", statsIndicatorHome);
		PropertyConfigurator.configure(log4jStream);
		log4jStream.close();

		try {
			LayerFactory layerFactory = new FolderLayerFactory(rootFolder);
			DataLocator dataLocator = new GeoserverDataLocator(gsDataFolder);
			Layer layer = layerFactory.newLayer(layerName);
			StatsIndicator statsIndicator = new StatsIndicator(dataLocator,
					layerFactory, layer);
			statsIndicator.run(PasswordGetterFactory.newPasswordGetter());
			System.out.println("The indicator was generated successfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error executing the indicator");
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
