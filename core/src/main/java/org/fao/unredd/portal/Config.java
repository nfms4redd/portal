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

	File getDir();

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

	ResourceBundle getMessages(Locale locale) throws ConfigurationException;

	String[] getPropertyAsArray(String property);

	String getDefaultLang();

	/**
	 * Plugin configuration provided by the list of
	 * {@link ModuleConfigurationProvider} By default one provider will read the
	 * plugin-conf.json file at the portal configuration folder
	 * 
	 * @param locale
	 * @param request
	 * @return
	 * @throws IOException
	 */
	Map<String, JSONObject> getPluginConfiguration(Locale locale,
			HttpServletRequest request) throws IOException;

	/**
	 * Add providers to modify the behavior of
	 * {@link #getPluginConfiguration(HttpServletRequest)} and
	 * 
	 * @param provider
	 */
	void addModuleConfigurationProvider(ModuleConfigurationProvider provider);

}
