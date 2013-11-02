package org.fao.unredd.portal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LoggingFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		System.out.println(request.getRemoteAddr() + " está accediendo a "
				+ ((HttpServletRequest) request).getRequestURL().toString());
		chain.doFilter(request, response);
		System.out.println("Respuesta a "
				+ ((HttpServletRequest) request).getRequestURL().toString()
				+ " servida");
	}

	@Override
	public void destroy() {
	}

}
