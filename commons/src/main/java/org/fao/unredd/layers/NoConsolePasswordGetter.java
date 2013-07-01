package org.fao.unredd.layers;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Implementation to use when there is no {@link Console} in the system
 * 
 * @author fergonco
 */
public class NoConsolePasswordGetter implements PasswordGetter {

	@Override
	public String getPassword(String connectionInfo) throws IOException {
		System.out.println("Connecting to " + connectionInfo);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		return reader.readLine();
	}

}
