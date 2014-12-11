package org.fao.unredd.feedback;

public class MissingArgumentException extends Exception {
	private static final long serialVersionUID = 1L;

	private String argumentName;

	public MissingArgumentException(String argumentName) {
		this.argumentName = argumentName;
	}

	public String getArgumentName() {
		return argumentName;
	}

}
