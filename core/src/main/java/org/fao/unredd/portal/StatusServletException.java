package org.fao.unredd.portal;

import javax.servlet.ServletException;

public class StatusServletException extends ServletException {
	private static final long serialVersionUID = 1L;

	private int status;

	public StatusServletException(int status, String msg) {
		this(status, msg, null);
	}

	public StatusServletException(int status, Throwable e) {
		this(status, e.getMessage(), e);
	}

	public StatusServletException(int status, String msg, Throwable e) {
		super(msg, e);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
