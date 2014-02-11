package org.fao.unredd.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class CachedProperties {

	private static Logger logger = Logger.getLogger(CachedProperties.class);

	private long lastRead = 0;
	private File propertyFile;
	private Properties properties;

	public CachedProperties(File propertyFile) {
		this.propertyFile = propertyFile;
	}

	public Properties getProperties() {
		long lastModified = propertyFile.lastModified();
		if (lastModified > lastRead) {
			logger.debug("Reading portal properties file " + propertyFile);
			properties = new Properties();
			try {
				properties.load(new FileInputStream(propertyFile));
			} catch (IOException e) {
				logger.error("Error reading portal properties file", e);
			}

			lastRead = lastModified;
		}

		return properties;
	}

}
