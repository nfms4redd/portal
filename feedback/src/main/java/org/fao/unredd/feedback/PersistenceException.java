package org.fao.unredd.feedback;


public class PersistenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersistenceException(String message, Exception cause) {
		super(message, cause);
	}
}
