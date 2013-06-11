package org.fao.unredd.layers;

import java.io.File;
import java.io.IOException;

public class MockLayer implements Layer {

	private OutputDescriptor output = new OutputDescriptor("zonal-stats",
			"Statistics", "id");

	@Override
	public Outputs getOutputs() {
		return new Outputs(output);
	}

	@Override
	public String getOutput(String outputId) {
		return "These are the statistics maaaaan";
	}

	@Override
	public File getWorkFile(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public File getDataFolder() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getConfiguration(String id)
			throws NoSuchConfigurationException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOutput(String id, String fieldId, String content)
			throws IOException {
		throw new UnsupportedOperationException();
	}

}
