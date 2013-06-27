package org.fao.unredd.layers;

public class ConsolePasswordGetter implements PasswordGetter {

	@Override
	public String getPassword(String connectionInfo) {
		System.out.println("Connecting to " + connectionInfo);
		return new String(System.console().readPassword("Password:"));
	}

}
