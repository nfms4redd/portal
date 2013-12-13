package org.fao.unredd.dbconf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchConfigurationException;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Output;
import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;
import org.fao.unredd.model.Indicator;

public class DBLayer implements Layer {

	private EntityManagerFactory emf;
	private String layerName;

	public DBLayer(EntityManagerFactory emf, String layerName) {
		this.emf = emf;
		this.layerName = layerName;
	}

	@Override
	public Outputs getOutputs() throws IOException {
		EntityManager entityManager = emf.createEntityManager();
		try {
			TypedQuery<Indicator> indicatorsQuery = entityManager.createQuery(
					"SELECT i FROM Indicator i WHERE i.layer.id ='" + layerName
							+ "'", Indicator.class);
			List<Indicator> indicators;
			indicators = indicatorsQuery.getResultList();
			ArrayList<OutputDescriptor> outputs = new ArrayList<OutputDescriptor>();
			for (Indicator indicator : indicators) {
				OutputDescriptor indicatorDescriptor = new OutputDescriptor(
						Integer.toString(indicator.getId()),
						indicator.getName(), "oid-120");
				outputs.add(indicatorDescriptor);
			}
			return new Outputs(outputs);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public Output getOutput(String outputId, String objectId)
			throws NoSuchIndicatorException, IOException {
		EntityManager entityManager = emf.createEntityManager();
		try {
			String jpql = "SELECT new org.fao.unredd.dbconf.DBOutput(i.contentType, d.content) "
					+ "FROM Indicator i, IndicatorData d "
					+ "WHERE i.id='"
					+ outputId
					+ "' AND i.layer.id ='"
					+ layerName
					+ "' "
					+ "AND d.indicator = i AND d.id='" + objectId + "'";
			TypedQuery<DBOutput> indicatorsQuery = entityManager.createQuery(
					jpql, DBOutput.class);
			return indicatorsQuery.getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	@Override
	public void setOutput(String id, String outputName, String fieldId,
			String content) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public File getWorkFile(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWorkspace() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQualifiedName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getConfiguration(String id)
			throws NoSuchConfigurationException, IOException {
		throw new UnsupportedOperationException();
	}

}
