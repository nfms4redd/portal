package org.fao.unredd.statsCalculator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;
import org.fao.unredd.charts.generated.DataType;
import org.fao.unredd.charts.generated.LabelType;
import org.fao.unredd.charts.generated.StatisticsChartInput;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.Location;
import org.fao.unredd.layers.PasswordGetter;
import org.fao.unredd.process.ProcessExecutionException;
import org.fao.unredd.statsCalculator.generated.PresentationDataType;
import org.fao.unredd.statsCalculator.generated.VariableType;

public class OutputBuilder {

	private Logger logger = Logger.getLogger(OutputBuilder.class);

	private Layer layer;
	private StatisticsChartInput chartInput;
	private String zoneIdField;
	private SimpleDateFormat timeFormat;
	private String indicatorName;

	public OutputBuilder(Layer layer, VariableType variable)
			throws ConfigurationException {
		this.layer = layer;
		PresentationDataType presentationData = variable.getPresentationData();
		chartInput = new StatisticsChartInput();
		chartInput.setTitle(presentationData.getTitle());
		chartInput.setSubtitle(presentationData.getSubtitle());
		chartInput.setFooter(presentationData.getFooter());
		chartInput.setHover(presentationData.getHover());
		chartInput.setTooltipDecimals(0);
		chartInput.setYLabel("Área");
		chartInput.setUnits("km<sup>2</sup>");
		chartInput.setLabels(new LabelType());

		zoneIdField = variable.getZoneIdField();
		indicatorName = variable.getPresentationData().getTitle();
		timeFormat = getTimeFormat(variable.getPresentationData());
	}

	private SimpleDateFormat getTimeFormat(PresentationDataType configuration)
			throws ConfigurationException {
		String dateFormat = configuration.getDateFormat();
		if (dateFormat != null) {
			try {
				return new SimpleDateFormat(dateFormat);
			} catch (IllegalArgumentException e) {
				throw new ConfigurationException(
						"The date format of the configuration is not valid: "
								+ dateFormat, e);
			}
		} else {
			return new SimpleDateFormat();
		}
	}

	public void addToOutput(File areaRaster, Date timestamp,
			File timestampFile, Location zonesLocation, int width, int height,
			PasswordGetter passwordGetter) throws IOException,
			ProcessExecutionException {

		chartInput.getLabels().getLabel().add(timeFormat.format(timestamp));
		File tempRasterized = File.createTempFile("raster", ".tiff");
		File tempMaskedAreaBands = File.createTempFile("masked_area_bands",
				".tiff");
		File tempMaskedArea = File.createTempFile("masked_areas", ".tiff");
		File tempStats = File.createTempFile("stats", ".txt");

		InputStream scriptStream = this.getClass().getResourceAsStream(
				"stats.sh");
		Script script = new Script(scriptStream);
		scriptStream.close();

		setParameterAndLog(script, "field", zoneIdField);
		setParameterAndLog(script, "width", width);
		setParameterAndLog(script, "height", height);
		setParameterAndLog(script, "layerName",
				zonesLocation.getGDALFeatureName());
		script.setParameter("rasterizeInput",
				zonesLocation.getGDALString(passwordGetter));
		logger.debug(zonesLocation.getGDALString(new PasswordGetter() {

			@Override
			public String getPassword(String connectionInfo) throws IOException {
				return "";
			}
		}));
		setParameterAndLog(script, "rasterizeOutput",
				tempRasterized.getAbsolutePath());
		setParameterAndLog(script, "areaRaster", areaRaster.getAbsolutePath());
		setParameterAndLog(script, "maskedAreaBands",
				tempMaskedAreaBands.getAbsolutePath());
		setParameterAndLog(script, "maskedArea",
				tempMaskedArea.getAbsolutePath());
		setParameterAndLog(script, "forestMask",
				timestampFile.getAbsolutePath());
		setParameterAndLog(script, "tempStats", tempStats.getAbsolutePath());

		script.run();

		BufferedReader br = new BufferedReader(new FileReader(tempStats));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split("\\s+");
			String id = parts[0];
			DataType data = getData(id, chartInput.getData());
			data.getValue().add(
					Double.parseDouble(parts[1]) * Double.parseDouble(parts[2])
							/ 1000000.0);
		}
		br.close();

		tempMaskedArea.delete();
		tempMaskedAreaBands.delete();
		tempRasterized.delete();
		tempStats.delete();
	}

	public void setParameterAndLog(Script script, String paramName, Object value) {
		script.setParameter(paramName, value);
		logger.debug("Setting parameter '" + paramName + "': " + value);
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

	public void writeResult(String mosaicLayerName) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXB.marshal(chartInput, baos);
		mosaicLayerName = mosaicLayerName.replaceAll("\\W+", "_");
		layer.setOutput(StatsIndicatorConstants.OUTPUT_ID_PREFIX + "_"
				+ mosaicLayerName, indicatorName, zoneIdField,
				new String(baos.toByteArray()));
	}

}
