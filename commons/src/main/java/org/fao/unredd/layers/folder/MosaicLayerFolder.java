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
package org.fao.unredd.layers.folder;

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

import org.fao.unredd.layers.Location;
import org.fao.unredd.layers.MosaicLayer;
import org.geotools.gce.imagemosaic.properties.time.TimeParser;

/**
 * Manages the configuration of image mosaic layers
 * 
 * @author fergonco
 */
public class MosaicLayerFolder extends AbstractLayerFolder implements
		MosaicLayer {

	private TreeMap<Date, File> files;

	/**
	 * Creates a new instance
	 * 
	 * @param layerName
	 *            Name of the layer
	 * @param folder
	 *            Folder containing the configuration
	 * @throws IOException
	 *             If the layer folder does not exist and cannot be created
	 */
	public MosaicLayerFolder(String layerName, File folder) throws IOException {
		super(layerName, folder);
	}

	/**
	 * Gets a map that associates the timestamps of the snapshots in the time
	 * mosaic with the tiff file that represents the snapshot
	 * 
	 * @throws InvalidFolderStructureException
	 *             If the folder does not match the expected structure for a
	 *             mosaic
	 * @throws IOException
	 *             If there is any problem analyzing the folder
	 */
	@Override
	public TreeMap<Date, File> getTimestamps(Location location)
			throws InvalidFolderStructureException, IOException {
		if (files == null) {
			File dataFolder = location.getFile();
			File[] snapshotFiles = dataFolder.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					String lowerCaseName = name.toLowerCase();
					return lowerCaseName.endsWith(".tiff")
							|| lowerCaseName.endsWith(".tif");
				}
			});
			Properties timeregexProperties = new Properties();
			File timeregexPropertiesFile = new File(dataFolder,
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
			files = new TreeMap<Date, File>();
			for (File snapshotFile : snapshotFiles) {
				String name = snapshotFile.getName();
				Matcher matcher = pattern.matcher(name);
				if (!matcher.find()) {
					throw new InvalidFolderStructureException(
							"The date of the snapshot could not be obtained",
							snapshotFile);
				}
				String dateString = matcher.group();
				try {
					TimeParser timeParser = new TimeParser();
					List<Date> dates = timeParser.parse(dateString);
					files.put(dates.get(0), snapshotFile);
				} catch (java.text.ParseException e) {
					throw new InvalidFolderStructureException(
							"The date of the snapshot "
									+ "could not be obtained: " + dateString,
							snapshotFile);
				}
			}
			if (files.isEmpty()) {
				throw new InvalidFolderStructureException(
						"There are no snapshots in the mosaic folder: "
								+ dataFolder.getAbsolutePath(), dataFolder);
			}
		}
		return files;
	}

}
