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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Same as {@link ReloadableResourceBundleMessageSource}, with a new
 * method to get all of the translated messages at once.
 * 
 * @author Oscar Fonts
 */
public class BundleMessage extends ReloadableResourceBundleMessageSource {

	public Map<String, String> getMessages(Locale locale) {
		Map<String, String> msg = new HashMap<String, String>();
        for(Entry<Object, Object> loc : getMergedProperties(locale).getProperties().entrySet()) {
        	String val = this.getMessage(loc.getKey().toString(), null, locale);
        	msg.put(loc.getKey().toString(), val);
        }
        return msg;
	}
}
