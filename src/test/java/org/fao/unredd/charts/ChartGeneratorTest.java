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
