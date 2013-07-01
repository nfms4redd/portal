package org.fao.unredd.layers;

import java.io.IOException;

/**
 * Interface to get a password, normally from console
 * 
 * @author fergonco
 */
public interface PasswordGetter {

	String getPassword(String connectionInfo) throws IOException;

}
