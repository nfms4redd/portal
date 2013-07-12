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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Utility class to access the custom resources placed in PORTAL_CONFIG_DIR.
 * 
 * @author Oscar Fonts
 */
@Component("config")
public class Config implements ServletContextAware {

	static Logger logger = Logger.getLogger(Config.class);

	ServletContext context;
	HttpServletRequest request;
	HttpServletResponse response;
	
	File dir = null;
	Properties properties = null;
    
    @Autowired
    BundleMessage messageSource;
	
	public void setServletContext(ServletContext servletContext) {
		this.context = servletContext;
	}

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
	
	@PostConstruct
    public void init() {
        context.setAttribute("config", this);
    }
	
	public File getDir() {
		if (dir == null) {
			String defaultDir = context.getRealPath("/") + File.separator + "WEB-INF" + File.separator + "default_config";
			
			// Get the portal config dir property from Java system properties
			String portalConfigDir = System.getProperty("PORTAL_CONFIG_DIR");
			
			// If not set in the system properties, get it from the Servlet context parameters (web.xml)
			if (portalConfigDir == null)
				portalConfigDir = context.getInitParameter("PORTAL_CONFIG_DIR");
			
			// Otherwise:
			if (portalConfigDir == null) {
				// if not set already, use the default portal config dir
				logger.warn("PORTAL_CONFIG_DIR property not found. Using default config.");
				dir = new File(defaultDir);
			} else {
				// if set but not existing, use the default portal config dir
				dir = new File(portalConfigDir);
				if (!dir.exists()) {
					logger.warn("PORTAL_CONFIG_DIR is set to " + dir.getAbsolutePath() +
							", but it doesn't exist. Using default config.");
					dir = new File(defaultDir);
				}
			}
				
			logger.info("============================================================================");
			logger.info("PORTAL_CONFIG_DIR: " + dir.getAbsolutePath());
			logger.info("============================================================================");
		}
		
		return dir;
	}
	
	public boolean isMinifiedJs() {
		return Boolean.parseBoolean(System.getProperty("MINIFIED_JS", "true"));
	}
	
	public Properties getProperties() {
		if (properties == null) {
			String location = getDir()+"/portal.properties";
			logger.debug("Reading portal properties file "+location);
			properties = new Properties();
			try {
			    properties.load(new FileInputStream(location));
			} catch (IOException e) {
				logger.error("Error reading portal properties file", e);
			}
		}
		return properties;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getLanguages() {
	    String json = getProperties().getProperty("languages", "{\"en\": \"English\"}");
		return (Map<String, String>)JSONObject.toBean(JSONObject.fromObject(json), java.util.HashMap.class);
	}
	
	public Map<String, String> getMessages() {
		return messageSource.getMessages(RequestContextUtils.getLocale(request));
	}
	
	public String getHeader() {
		return getLocalizedFileContents(new File(getDir()+"/header.tpl"));
	}
	
	public String getFooter() {
		return getLocalizedFileContents(new File(getDir()+"/footer.tpl"));
	}
	
	public String getLayers() {
		return getLocalizedFileContents(new File(getDir()+"/layers.json"));
	}
	
	String getLocalizedFileContents(File file) {
		try {
			String template = new String(getFileContents(file), "UTF-8");
			Pattern patt = Pattern.compile("\\$\\{([\\w.]*)\\}");
			Matcher m = patt.matcher(template);
			StringBuffer sb = new StringBuffer(template.length());
			while (m.find()) {
				String text = getMessages().get(m.group(1));
				if (text != null) {
					m.appendReplacement(sb, text);
				}
			}
			m.appendTail(sb);
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported encoding", e);
			return "";
		}
	}
	
	byte[] getFileContents(File file) {
		byte[] result = new byte[(int) file.length()];
		try {
			InputStream input = null;
			try {
				int totalBytesRead = 0;
				input = new BufferedInputStream(new FileInputStream(file));
				while (totalBytesRead < result.length) {
					int bytesRemaining = result.length - totalBytesRead;
					// input.read() returns -1, 0, or more :
					int bytesRead = input.read(result, totalBytesRead,
							bytesRemaining);
					if (bytesRead > 0) {
						totalBytesRead = totalBytesRead + bytesRead;
					}
				}
			} finally {
				input.close();
			}
		} catch (FileNotFoundException ex) {
			logger.error("File not found.", ex);
		} catch (IOException ex) {
			logger.error("Error reading file contents.", ex);
		}
		return result;
	}
}
