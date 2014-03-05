package org.fao.unredd.portal;

import javax.servlet.ServletException;

public class StatusServletException extends ServletException {
	private static final long serialVersionUID = 1L;

	private int status;

	public StatusServletException(int status, String msg) {
		super(msg);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
