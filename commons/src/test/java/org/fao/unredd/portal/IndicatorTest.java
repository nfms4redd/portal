package org.fao.unredd.portal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.fao.unredd.layers.Indicator;
import org.fao.unredd.layers.Indicators;
import org.junit.Test;

public class IndicatorTest {

	@Test
	public void testExpectedJSONSerialization() throws Exception {
		Indicators indicators = new Indicators(new Indicator("a", "b",
				"application/json", "[{}]"));

		ObjectMapper mapper = new ObjectMapper();
		String json = indicators.toJSON();
		System.out.println(json);
		JsonNode tree = mapper.readTree(json);
		assertEquals(1, tree.size());
		JsonNode indicatorNode = tree.get(0);
		assertEquals("a", indicatorNode.get("id").asText());
		assertEquals("b", indicatorNode.get("name").asText());
		Iterator<String> nameIterator = indicatorNode.getFieldNames();
		nameIterator.next();
		nameIterator.next();
		assertTrue(!nameIterator.hasNext());
	}
}
