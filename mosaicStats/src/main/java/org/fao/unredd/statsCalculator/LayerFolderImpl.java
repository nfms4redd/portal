package org.fao.unredd.statsCalculator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fao.unredd.layers.Layer;

/**
 * Basic concrete implementation of the {@link Layer} interface based on folders
 * 
 * @author fergonco
 */
public class LayerFolderImpl extends AbstractLayerFolder {

	/**
	 * Builds a new instance
	 * 
	 * @param folder
	 * @throws IllegalArgumentException
	 *             If the folder does not exist
	 * @throws InvalidFolderStructureException
	 *             If the layer does not follow the expected rules
	 */
	public LayerFolderImpl(File folder) throws IllegalArgumentException,
			InvalidFolderStructureException {
		super(folder);
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
			statsIndicator.analyze();
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
