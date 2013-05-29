package org.fao.unredd.statsCalculator;

/**
 * Signals that the content of the configuration file is wrong in some sense
 * described in the message of the exception
 * 
 * @author fergonco
 */
public class ConfigurationException extends Exception {
	private static final long serialVersionUID = 1L;

	public ConfigurationException(String msg) {
		super(msg);
	}
}
