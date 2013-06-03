package org.fao.unredd.layers;

public class MockLayerFactory implements LayerFactory {

	@Override
	public Layer newLayer(String layerName) {
		return new MockLayer();
	}

	@Override
	public MosaicLayer newMosaicLayer(String layer)
			throws NoSuchGeoserverLayerException {
		throw new UnsupportedOperationException();
	}

}
