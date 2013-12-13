package org.fao.unredd.dbconf;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.MosaicLayer;

public class DBConf implements LayerFactory {

	private EntityManagerFactory emf;

	public DBConf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Override
	public Layer newLayer(String layerName) throws IOException {
		return new DBLayer(emf, layerName);
	}

	@Override
	public MosaicLayer newMosaicLayer(String layer) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists(String layerName) {
		EntityManager entityManager = emf.createEntityManager();
		try {
			org.fao.unredd.model.Layer layer = entityManager.find(
					org.fao.unredd.model.Layer.class, layerName);
			boolean exists = layer != null;
			return exists;
		} finally {
			entityManager.close();
		}
	}
}
