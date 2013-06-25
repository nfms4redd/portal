package org.fao.unredd.layers;

import java.io.IOException;

/**
 * Interface that obtains a {@link Location} instance to locate the place where
 * {@link Layer}s have their data
 * 
 * @author fergonco
 */
public interface DataLocator {

	/**
	 * @param layer
	 * @return
	 * @throws IOException
	 *             If there is a problem obtaining the layer location
	 * @throws CannotFindLayerException
	 *             If the layer location cannot be found
	 */
	Location locate(Layer layer) throws IOException, CannotFindLayerException;

}
