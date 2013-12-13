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
package org.fao.unredd.portal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class IndicatorsTest {

	private static String CONTENT = null;
	private HttpServletResponse response;
	private LayerFactory layerFactory;
	private StringWriter responseWriter;
	private IndicatorsController indicators;

	@BeforeClass
	public static void setupClass() throws Exception {
		InputStream stream = IndicatorsTest.class
				.getResourceAsStream("sample-output.xml");
		CONTENT = IOUtils.toString(stream);
		stream.close();
	}

	@Before
	public void setupCommon() throws Exception {
		response = mock(HttpServletResponse.class);
		responseWriter = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

		Layer layer = mock(Layer.class);
		OutputDescriptor indicator = new OutputDescriptor("id", "name",
				"fieldId");
		when(layer.getOutputs()).thenReturn(new Outputs(indicator));
		// when(layer.getOutput("id")).thenReturn(CONTENT);
		// when(layer.getOutput(argThat(new BaseMatcher<String>() {
		//
		// @Override
		// public boolean matches(Object argument) {
		// return !argument.equals("id");
		// }
		//
		// @Override
		// public void describeTo(Description description) {
		// description.appendText("Different from id");
		// }
		// }))).thenThrow(new NoSuchIndicatorException(""));
		layerFactory = mock(LayerFactory.class);
		when(layerFactory.exists(anyString())).thenReturn(true);
		when(layerFactory.newLayer(anyString())).thenReturn(layer);
		indicators = new IndicatorsController(response, layerFactory);
	}

	@Test
	public void testIndicatorsOk() throws Exception {
		indicators.returnIndicators("thelayerid");

		verify(response, never()).setStatus(anyInt());
		ArgumentCaptor<String> contentTypeCaptor = ArgumentCaptor
				.forClass(String.class);
		verify(response).setContentType(contentTypeCaptor.capture());
		assertTrue(contentTypeCaptor.getValue().contains("json"));
		String output = responseWriter.getBuffer().toString();
		assertTrue(output.matches("\\[\\s*\\{.*"));
	}

	@Test
	public void testIndicatorOk() throws Exception {
		indicators.returnIndicator("1", "thelayerid", "id");

		verify(response, never()).setStatus(anyInt());
		verify(response).setContentType(contains("text/html"));
		assertTrue(responseWriter.getBuffer().toString().contains("html"));
	}

	@Test
	public void testIndicatorNotFound() throws Exception {
		indicators.returnIndicator("1", "thelayerid", "notfound");
		verify(response).setStatus(
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT.getStatus());
	}

	@Test
	public void testIndicatorsNullLayerId() throws Exception {
		indicators.returnIndicators(null);
		verify(response).setStatus(
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT.getStatus());
	}

	@Test
	public void testIndicatorsUnexistantLayer() throws Exception {
		when(layerFactory.exists(anyString())).thenReturn(false);
		indicators.returnIndicators("");
		verify(layerFactory, never()).newLayer(anyString());
		verify(response, never()).setStatus(anyInt());
	}

	@Test
	public void testIndicatorNullLayerId() throws Exception {
		indicators.returnIndicator("1", null, "indicatorId");
		verify(response).setStatus(
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT.getStatus());
	}

	@Test
	public void testIndicatorNullIndicatorId() throws Exception {
		indicators.returnIndicator("1", "layerId", null);
		verify(response).setStatus(
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT.getStatus());
	}

	@Test
	public void testIndicatorNullObjectId() throws Exception {
		indicators.returnIndicator(null, "layerId", "indicatorId");
		verify(response).setStatus(
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT.getStatus());
	}

	@Test
	public void testIndicatorUnexistantLayer() throws Exception {
		when(layerFactory.exists(anyString())).thenReturn(false);
		indicators.returnIndicator("1", "layerId", "indicatorId");
		verify(layerFactory, never()).newLayer(anyString());
	}
}
