package org.fao.unredd.statsCalculator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;

import org.fao.unredd.charts.generated.DataType;
import org.fao.unredd.charts.generated.LabelType;
import org.fao.unredd.charts.generated.StatisticsChartInput;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.process.ProcessExecutionException;
import org.fao.unredd.statsCalculator.generated.PresentationDataType;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;

public class OutputBuilder {

	private Layer layer;
	private StatisticsChartInput chartInput;
	private String zoneIdField;

	public OutputBuilder(Layer layer, ZonalStatistics statisticsConfiguration) {
		this.layer = layer;
		PresentationDataType presentationData = statisticsConfiguration
				.getPresentationData();
		chartInput = new StatisticsChartInput();
		chartInput.setTitle(presentationData.getTitle());
		chartInput.setSubtitle(presentationData.getSubtitle());
		chartInput.setFooter(presentationData.getFooter());
		chartInput.setHover(presentationData.getHover());
		chartInput.setTooltipDecimals(0);
		chartInput.setYLabel("Área");
		chartInput.setUnits("km<sup>2</sup>");
		chartInput.setLabels(new LabelType());

		zoneIdField = statisticsConfiguration.getZoneIdField();
	}

	public void addToOutput(File areaRaster, String timestampText,
			File timestampFile, File zones, int width, int height)
			throws IOException, ProcessExecutionException {

		chartInput.getLabels().getLabel().add(timestampText);
		File tempRasterized = File.createTempFile("raster", ".tiff");
		File tempMaskedAreaBands = File.createTempFile("masked_area_bands",
				".tiff");
		File tempMaskedArea = File.createTempFile("masked_areas", ".tiff");
		File tempStats = File.createTempFile("stats", ".txt");

		InputStream scriptStream = this.getClass().getResourceAsStream(
				"stats.sh");
		Script script = new Script(scriptStream);
		scriptStream.close();

		script.setParameter("field", zoneIdField);
		script.setParameter("width", width);
		script.setParameter("height", height);
		script.setParameter("layerName",
				zones.getName().substring(0, zones.getName().length() - 4));
		script.setParameter("rasterizeInput", zones.getAbsolutePath());
		script.setParameter("rasterizeOutput", tempRasterized.getAbsolutePath());
		script.setParameter("areaRaster", areaRaster.getAbsolutePath());
		script.setParameter("maskedAreaBands",
				tempMaskedAreaBands.getAbsolutePath());
		script.setParameter("maskedArea", tempMaskedArea.getAbsolutePath());
		script.setParameter("forestMask", timestampFile.getAbsolutePath());
		script.setParameter("tempStats", tempStats.getAbsolutePath());

		script.run();

		BufferedReader br = new BufferedReader(new FileReader(tempStats));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split("\\s+");
			String id = parts[0];
			DataType data = getData(id, chartInput.getData());
			data.getValue()
					.add(Double.parseDouble(parts[1])
							* Double.parseDouble(parts[2]));
		}
		br.close();

		tempMaskedArea.delete();
		tempMaskedAreaBands.delete();
		tempRasterized.delete();
		tempStats.delete();
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

	public void writeResult() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXB.marshal(chartInput, baos);
		layer.setOutput(StatsIndicatorConstants.OUTPUT_ID, zoneIdField,
				new String(baos.toByteArray()));
	}

}
