package org.fao.unredd.layers;

import java.io.IOException;

public interface PasswordGetter {

	String getPassword(String connectionInfo) throws IOException;

}
