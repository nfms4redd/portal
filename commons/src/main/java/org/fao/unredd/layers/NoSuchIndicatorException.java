package org.fao.unredd.layers;

/**
 * Indicates the specified output does not exist in the layer
 * 
 * @author fergonco
 */
public class NoSuchIndicatorException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoSuchIndicatorException(String outputId) {
		super(outputId);
	}

}
