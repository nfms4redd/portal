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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

public class DBLocationTest {

	@Test
	public void testConnectionString() throws IOException {
		DBLocation location = new DBLocation("localhost", "4322", "geoserver",
				"gis", "provinces", "fernan");
		PasswordGetter passwordGetter = mock(PasswordGetter.class);
		when(passwordGetter.getPassword(anyString())).thenReturn("pass");
		assertEquals("PG:\"host=localhost port=4322 dbname=geoserver "
				+ "user=fernan password=pass\"",
				location.getGDALString(passwordGetter));
	}
}
