package org.fao.unredd.portal;

import javax.servlet.ServletException;

public class IllegalUsageServletException extends ServletException {
	private static final long serialVersionUID = 1L;

	public IllegalUsageServletException(String msg) {
		super(msg);
	}

}
