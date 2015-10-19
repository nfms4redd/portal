package org.fao.unredd.portal;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.fao.unredd.AppContextListener;
import org.fao.unredd.jwebclientAnalyzer.Context;
import org.junit.Before;
import org.junit.Test;

public class AppContextListenerTest {
	private AppContextListener listener;

	@Before
	public void setup() {
		listener = new AppContextListener();
	}

	@Test
	public void addsClientDirsToContext() {
		ServletContext context = mock(ServletContext.class);
		when(context.getRealPath(anyString())).thenReturn("__mock__");

		listener.contextInitialized(new ServletContextEvent(context));
		verify(context).setAttribute("client-dirs",
				Context.DEFAULT_CLIENT_DIRECTORIES);
	}
}
