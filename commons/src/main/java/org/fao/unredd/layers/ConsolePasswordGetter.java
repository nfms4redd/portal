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
package org.fao.unredd.layers;

import java.io.Console;

/**
 * Implementation when there is a {@link Console} instance
 * 
 * @author fergonco
 */
public class ConsolePasswordGetter implements PasswordGetter {

	@Override
	public String getPassword(String connectionInfo) {
		System.out.println("Connecting to " + connectionInfo);
		return new String(System.console().readPassword("Password:"));
	}

}
