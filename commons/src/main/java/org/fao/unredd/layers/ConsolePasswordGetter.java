package org.fao.unredd.layers;

import java.io.Console;

/**
 * Implementation when there is a {@link Console} instance
 * 
 * @author fergonco
 */
public class ConsolePasswordGetter implements PasswordGetter {

	@Override
	public String getPassword(String connectionInfo) {
		System.out.println("Connecting to " + connectionInfo);
		return new String(System.console().readPassword("Password:"));
	}

}
