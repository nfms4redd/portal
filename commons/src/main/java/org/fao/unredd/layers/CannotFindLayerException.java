package org.fao.unredd.layers;

/**
 * The {@link DataLocator} of the layer cannot be obtained
 * 
 * @author fergonco
 */
public class CannotFindLayerException extends Exception {
	private static final long serialVersionUID = 1L;

	public CannotFindLayerException(String layerName) {
		super("The location of the layer cannot be found: " + layerName);
	}
}
