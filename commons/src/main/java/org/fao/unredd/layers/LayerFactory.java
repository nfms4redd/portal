package org.fao.unredd.layers;

public interface LayerFactory {

	/**
	 * Returns an instance to manage the layer that has the specified name
	 * 
	 * @param layerName
	 * @return
	 */
	Layer newLayer(String layerName);

}
