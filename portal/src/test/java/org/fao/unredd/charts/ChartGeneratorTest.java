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
package org.fao.unredd.charts;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ChartGeneratorTest {

	@Test
	public void testOmittedParameters() throws Exception {
		ChartGenerator chartGenerator = new ChartGenerator(this.getClass()
				.getResourceAsStream("chart-input-omitted-values.xml"));
		StringWriter writer = new StringWriter();
		chartGenerator.generate("1", writer);
		Pattern p = Pattern.compile("\\$[a-z]+");
		Matcher matcher = p.matcher(writer.toString());
		assertTrue(!matcher.find());
	}
}
