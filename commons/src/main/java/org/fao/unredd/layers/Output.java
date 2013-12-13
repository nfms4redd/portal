package org.fao.unredd.layers;

import java.io.IOException;
import java.io.OutputStream;

public interface Output {

	/**
	 * Get the content type of this output
	 * 
	 * @return
	 */
	String getContentType();

	/**
	 * Writes this output to the specified
	 * 
	 * @param os
	 * @throws IOException
	 */
	void writeOutput(OutputStream os) throws IOException;

}
