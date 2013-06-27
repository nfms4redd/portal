package org.fao.unredd.layers;

public class PasswordGetterFactory {

	public static PasswordGetter newPasswordGetter() {
		if (System.console() == null) {
			return new NoConsolePasswordGetter();
		} else {
			return new ConsolePasswordGetter();
		}
	}
}
