package org.fao.unredd.layers;

public class NoSuchIndicatorException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoSuchIndicatorException(String outputId) {
		super(outputId);
	}

}
