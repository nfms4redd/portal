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
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class OutputTest {

	@Test
	public void testSerialization() throws Exception {
		Outputs indicators = new Outputs(new Output("a", "b", "c","d"));

		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree = mapper.readTree(indicators.toJSON());
		assertEquals(1, tree.size());
		JsonNode indicatorNode = tree.get(0);
		assertEquals("a", indicatorNode.get("id").asText());
		assertEquals("b", indicatorNode.get("name").asText());
		assertEquals("c", indicatorNode.get("fieldId").asText());
		assertEquals("d", indicatorNode.get("title").asText());
		Iterator<String> nameIterator = indicatorNode.getFieldNames();
		nameIterator.next();
		nameIterator.next();
		nameIterator.next();
		assertTrue(!nameIterator.hasNext());
	}
}
