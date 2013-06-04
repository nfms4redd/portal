package org.fao.unredd.process;

public class ProcessExecutionException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProcessExecutionException(String[] cmd) {
		super(cmd[0]);
	}

	public ProcessExecutionException(String errorMessage) {
		super(errorMessage);
	}
}
