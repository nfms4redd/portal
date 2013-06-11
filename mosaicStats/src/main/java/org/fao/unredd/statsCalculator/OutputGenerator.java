package org.fao.unredd.statsCalculator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXB;

import org.fao.unredd.charts.generated.DataType;
import org.fao.unredd.charts.generated.LabelType;
import org.fao.unredd.charts.generated.StatisticsChartInput;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.process.ProcessExecutionException;
import org.fao.unredd.process.ProcessRunner;
import org.fao.unredd.statsCalculator.generated.PresentationDataType;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;

public class OutputGenerator {

	private Layer layer;

	public OutputGenerator(Layer layer) {
		this.layer = layer;
	}

	public void generateOutput(File areaRaster, String timestampText,
			File timestampFile, File zones,
			ZonalStatistics statisticsConfiguration, int width, int height)
			throws IOException, ProcessExecutionException {
		PresentationDataType presentationData = statisticsConfiguration
				.getPresentationData();
		StatisticsChartInput input = new StatisticsChartInput();
		input.setTitle(presentationData.getTitle());
		input.setSubtitle(presentationData.getSubtitle());
		input.setFooter(presentationData.getFooter());
		input.setHover(presentationData.getHover());
		input.setTooltipDecimals(0);
		input.setYLabel("Área");
		input.setUnits("km<sup>2</sup>");
		input.setLabels(new LabelType());

		input.getLabels().getLabel().add(timestampText);
		File tempRaster = File.createTempFile("raster", ".tiff");
		File tempStats = File.createTempFile("stats", ".txt");
		new ProcessRunner("gdal_rasterize", "-a",
				statisticsConfiguration.getZoneIdField(), "-ot", "Byte", "-ts",
				Integer.toString(width), Integer.toString(height), "-l", zones
						.getName().substring(0, zones.getName().length() - 4),
				zones.getAbsolutePath(), tempRaster.getAbsolutePath()).run();
		new ProcessRunner("oft-stat", "-i", areaRaster.getAbsolutePath(),
				"-um", tempRaster.getAbsolutePath(), "-o",
				tempStats.getAbsolutePath()).run();

		BufferedReader br = new BufferedReader(new FileReader(tempStats));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split("\\s+");
			String id = parts[0];
			DataType data = getData(id, input.getData());
			data.getValue()
					.add(Double.parseDouble(parts[1])
							* Double.parseDouble(parts[2]));
		}
		br.close();

		tempRaster.delete();
		tempStats.delete();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXB.marshal(input, baos);
		layer.setOutput(StatsIndicatorConstants.OUTPUT_ID,
				statisticsConfiguration.getZoneIdField(),
				new String(baos.toByteArray()));
	}

	private DataType getData(String id, List<DataType> data) {
		DataType ret = null;
		for (DataType dataType : data) {
			if (dataType.getZoneId().equals(id)) {
				ret = dataType;
			}
		}

		if (ret == null) {
			ret = new DataType();
			ret.setZoneId(id);
			data.add(ret);
		}

		return ret;
	}

}
