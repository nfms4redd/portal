package org.fao.unredd.process;

import org.apache.commons.lang.StringUtils;

public class ProcessExecutionException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProcessExecutionException(String[] command) {
		super(StringUtils.join(command, " "));
	}

	public ProcessExecutionException(String[] command, String errorMessage) {
		super(StringUtils.join(command, " ") + "\n" + errorMessage);
	}

	public ProcessExecutionException(String[] command, Exception e) {
		super(StringUtils.join(command, " "), e);
	}
}
