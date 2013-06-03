package org.fao.unredd.layers;

import java.io.File;

public class MockLayer implements Layer {

	private Output output = new Output("zonal-stats", "Statistics",
			"text/html", "These are the statistics maaaaan");

	@Override
	public Outputs getOutputs() {
		return new Outputs(output);
	}

	@Override
	public Output getOutput(String outputId) {
		return output;
	}

	@Override
	public File getWorkFile(String id) {
		throw new UnsupportedOperationException();
	}

}
