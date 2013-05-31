package org.fao.unredd.portal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.layers.Indicator;
import org.fao.unredd.layers.Indicators;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class IndicatorsTest {

	private static final String CONTENT = "content";
	private static final String CONTENT_TYPE = "contentType";
	private HttpServletResponse response;
	private LayerFactory layerFactory;
	private StringWriter responseWriter;

	@Before
	public void setupCommon() throws IOException {
		response = mock(HttpServletResponse.class);
		responseWriter = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

		Layer layer = mock(Layer.class);
		when(layer.getIndicators()).thenReturn(
				new Indicators(new Indicator("id", "name", CONTENT_TYPE,
						CONTENT)));
		layerFactory = mock(LayerFactory.class);
		when(layerFactory.newLayer(anyString())).thenReturn(layer);
	}

	@Test
	public void testOk() throws Exception {
		IndicatorsController indicators = new IndicatorsController(response,
				layerFactory);
		indicators.returnIndicators("thelayerid");

		verify(response, never()).setStatus(anyInt());
		ArgumentCaptor<String> contentTypeCaptor = ArgumentCaptor
				.forClass(String.class);
		verify(response).setContentType(contentTypeCaptor.capture());
		assertTrue(contentTypeCaptor.getValue().contains("json"));
		assertTrue(responseWriter.getBuffer().toString()
				.matches("\\[\\s*\\{.*"));
	}

	@Test
	public void testNullParameters() throws Exception {
		IndicatorsController indicators = new IndicatorsController(response,
				layerFactory);
		try {
			indicators.returnIndicators(null);
			fail();
		} catch (NullPointerException e) {
		}
	}

}
