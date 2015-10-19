package org.fao.unredd.portal;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class ClientContentServletTest {
	private ClientContentServlet servlet;
	private ServletContext servletContext;
	private Config config;

	@Before
	public void setup() throws ServletException {
		this.config = mock(Config.class);

		this.servletContext = mock(ServletContext.class);
		when(this.servletContext.getAttribute("config"))
				.thenReturn(this.config);

		this.servlet = spy(new ClientContentServlet());
		this.servlet.init(mock(ServletConfig.class));
		when(this.servlet.getServletContext()).thenReturn(this.servletContext);
	}

	@Test
	public void looksForResourcesInAllClientDirs() throws Exception {
		when(this.config.getDir()).thenReturn(new File("invalid__dir"));
		when(this.servletContext.getAttribute("client-dirs")).thenReturn(
				new String[] { "client" });

		MockServletOutputStream output = new MockServletOutputStream();

		HttpServletRequest request = mockRequest("", "/modules/module.js");
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(output);

		this.servlet.doGet(request, response);

		String expected = IOUtils.toString(getClass().getResourceAsStream(
				"/client/modules/module.js"));
		assertEquals(expected, new String(output.delegate.toByteArray()));
	}

	private HttpServletRequest mockRequest(String servletPath, String pathInfo) {
		HttpServletRequest request = mock(HttpServletRequest.class);

		when(request.getServletPath()).thenReturn(servletPath);
		when(request.getPathInfo()).thenReturn(pathInfo);

		return request;
	}

	private class MockServletOutputStream extends ServletOutputStream {
		private ByteArrayOutputStream delegate = new ByteArrayOutputStream();

		@Override
		public void write(int b) throws IOException {
			this.delegate.write(b);
		}
	}
}
