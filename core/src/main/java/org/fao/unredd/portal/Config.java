/*
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
package org.fao.unredd.portal;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public interface Config {

	public static final String PROPERTY_CLIENT_MODULES = "client.modules";
	public static final String PROPERTY_MAP_CENTER = "map.centerLonLat";
	public static final String PROPERTY_LANGUAGES = "languages";

	File getPortalPropertiesFile();

	File getDir();

	boolean isMinifiedJs();

	Properties getProperties();

	/**
	 * Returns an array of <code>Map&lt;String, String&gt;</code>. For each
	 * element of the array, a {@link Map} is returned containing two
	 * keys/values: <code>code</code> (for language code) and <code>name</code>
	 * (for language name).
	 * 
	 * @return
	 */
	Map<String, String>[] getLanguages();

	String getLayers(Locale locale, HttpServletRequest request)
			throws IOException, ConfigurationException;

	ResourceBundle getMessages(Locale locale) throws ConfigurationException;

	String getLocalizedFileContents(File file, Locale locale)
			throws IOException, ConfigurationException;

	String[] getPropertyAsArray(String property);

	String getDefaultLang();

	String getIndicatorsFolder();

	Map<String, JSONObject> getPluginConfiguration(HttpServletRequest request)
			throws IOException;

	void addModuleConfigurationProvider(ModuleConfigurationProvider provider);

}
