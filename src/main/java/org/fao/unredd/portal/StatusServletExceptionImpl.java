package org.fao.unredd.portal;

import javax.servlet.ServletException;

public class StatusServletExceptionImpl extends ServletException implements
		StatusServletException {
	private static final long serialVersionUID = 1L;

	private int status;

	public StatusServletExceptionImpl(int status, String msg) {
		super(msg);
		this.status = status;
	}

	@Override
	public int getStatus() {
		return status;
	}

}
