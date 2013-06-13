package org.fao.unredd.layers;

/**
 * Signals the absence of a layer on the NFMS system
 * 
 * @author fergonco
 */
public class NoSuchLayerException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoSuchLayerException(String layerName) {
		super(layerName);
	}
}
