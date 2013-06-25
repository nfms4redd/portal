package org.fao.unredd.layers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class DBLocationTest {

	@Test
	public void testConnectionString() {
		DBLocation location = new DBLocation("localhost", "4322", "geoserver",
				"gis", "provinces", "fernan");
		PasswordGetter passwordGetter = mock(PasswordGetter.class);
		when(passwordGetter.getPassword()).thenReturn("pass");
		assertEquals("PG:\"host=localhost port=4322 dbname=geoserver "
				+ "user=fernan password=pass\"",
				location.getGDALString(passwordGetter));
	}
}
