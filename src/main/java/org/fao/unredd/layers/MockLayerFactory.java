package org.fao.unredd.layers;

public class MockLayerFactory implements LayerFactory {

	@Override
	public Layer newLayer(String layerName) {
		return new MockLayer();
	}

}
