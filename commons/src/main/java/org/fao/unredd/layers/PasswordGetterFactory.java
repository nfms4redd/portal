package org.fao.unredd.layers;

import java.io.Console;

/**
 * Factory just to hold a method to instantiate the PasswordGetter depending on
 * whether there is a {@link Console} available or not
 * 
 * @author fergonco
 */
public class PasswordGetterFactory {

	public static PasswordGetter newPasswordGetter() {
		if (System.console() == null) {
			return new NoConsolePasswordGetter();
		} else {
			return new ConsolePasswordGetter();
		}
	}
}
