package org.fao.unredd.portal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.layers.Output;
import org.fao.unredd.layers.Outputs;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class IndicatorsTest {

	private static final String CONTENT = "content";
	private static final String CONTENT_TYPE = "contentType";
	private HttpServletResponse response;
	private LayerFactory layerFactory;
	private StringWriter responseWriter;
	private IndicatorsController indicators;

	@Before
	public void setupCommon() throws Exception {
		response = mock(HttpServletResponse.class);
		responseWriter = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

		Layer layer = mock(Layer.class);
		Output indicator = new Output("id", "name", CONTENT_TYPE, CONTENT);
		when(layer.getOutputs()).thenReturn(new Outputs(indicator));
		when(layer.getOutput("id")).thenReturn(indicator);
		when(layer.getOutput(argThat(new BaseMatcher<String>() {

			@Override
			public boolean matches(Object argument) {
				return !argument.equals("id");
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Different from id");
			}
		}))).thenThrow(new NoSuchIndicatorException());
		layerFactory = mock(LayerFactory.class);
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
		assertTrue(responseWriter.getBuffer().toString()
				.matches("\\[\\s*\\{.*"));
	}

	@Test
	public void testIndicatorOk() throws Exception {
		indicators.returnIndicator("thelayerid", "id");

		verify(response, never()).setStatus(anyInt());
		verify(response).setContentType(CONTENT_TYPE);
		assertTrue(responseWriter.getBuffer().toString().equals(CONTENT));
	}

	@Test
	public void testIndicatorNotFound() throws Exception {
		indicators.returnIndicator("thelayerid", "notfound");
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
	public void testIndicatorNullLayerId() throws Exception {
		indicators.returnIndicator(null, "indicatorId");
		verify(response).setStatus(
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT.getStatus());
	}

	@Test
	public void testIndicatorNullIndicatorId() throws Exception {
		indicators.returnIndicator("layerId", null);
		verify(response).setStatus(
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT.getStatus());
	}

}
