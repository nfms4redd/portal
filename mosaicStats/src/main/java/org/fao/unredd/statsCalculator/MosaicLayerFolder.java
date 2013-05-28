package org.fao.unredd.statsCalculator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.gce.imagemosaic.properties.time.TimeParser;

public class MosaicLayerFolder extends AbstractLayerFolder {

	public MosaicLayerFolder(File folder) throws NotAMosaicException {
		super(folder);
	}

	public TreeMap<Date, File> getTimestamps()
			throws InvalidFolderStructureException, IOException,
			SnapshotNamingException {
		File[] snapshotFiles = getDataFolder().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				String lowerCaseName = name.toLowerCase();
				return lowerCaseName.endsWith(".tiff")
						|| lowerCaseName.endsWith(".tif");
			}
		});
		Properties timeregexProperties = new Properties();
		File timeregexPropertiesFile = new File(getDataFolder(),
				"timeregex.properties");
		try {
			timeregexProperties.load(new FileInputStream(
					timeregexPropertiesFile));
		} catch (FileNotFoundException e) {
			throw new InvalidFolderStructureException(
					"The folder does not contain a timeregex.properties"
							+ " file in the 'mosaic' subfolder",
					timeregexPropertiesFile);
		}
		String timeregex = timeregexProperties.getProperty("regex");
		if (timeregex == null) {
			throw new InvalidFolderStructureException(
					"The timeregex.properties file does not contain"
							+ " the regex property in the folder",
					timeregexPropertiesFile);
		}
		Pattern pattern = Pattern.compile(timeregex);
		TreeMap<Date, File> files = new TreeMap<Date, File>();
		for (File snapshotFile : snapshotFiles) {
			String name = snapshotFile.getName();
			Matcher matcher = pattern.matcher(name);
			if (!matcher.find()) {
				throw new SnapshotNamingException("The date of the snapshot "
						+ "could not be obtained: "
						+ snapshotFile.getAbsolutePath());
			}
			String dateString = matcher.group();
			try {
				TimeParser timeParser = new TimeParser();
				List<Date> dates = timeParser.parse(dateString);
				files.put(dates.get(0), snapshotFile);
			} catch (java.text.ParseException e) {
				throw new SnapshotNamingException("The date of the snapshot "
						+ "could not be obtained: " + dateString);
			}
		}
		if (files.isEmpty()) {
			throw new InvalidFolderStructureException(
					"There are no snapshots in the mosaic folder: "
							+ getDataFolder().getAbsolutePath(),
					getDataFolder());
		}

		return files;
	}

}
