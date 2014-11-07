package org.fao.unredd.layers.bd;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Outputs;

public class DBLayer implements Layer {

	public DBLayer(String layerName) {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public Outputs getOutputs() throws IOException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array getOutput(String outputId) throws NoSuchIndicatorException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
