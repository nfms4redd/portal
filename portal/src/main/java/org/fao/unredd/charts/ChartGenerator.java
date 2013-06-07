package org.fao.unredd.charts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.fao.unredd.charts.generated.StatisticsChartInput;
import org.fao.unredd.charts.generated.ValueType;

/**
 * Hello world!
 * 
 */
public class ChartGenerator {

	private StatisticsChartInput inputData;

	public ChartGenerator(InputStream chartInput) {
		inputData = JAXB.unmarshal(chartInput, StatisticsChartInput.class);
	}

	public void generate(Writer writer) throws IOException {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty("resource.loader", "class");
		engine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		engine.init();
		VelocityContext context = new VelocityContext();
		context.put("title", inputData.getTitle());
		context.put("subtitle", inputData.getSubtitle());
		context.put("dates", getLabels(inputData.getValue()));
		context.put("y-label", inputData.getYLabel());
		context.put("units", inputData.getUnits());
		context.put("tooltipDecimals", inputData.getTooltipDecimals());
		context.put("data", getValues(inputData.getValue()));
		context.put("hover", inputData.getHover());
		context.put("footer", inputData.getFooter());

		Template t = engine
				.getTemplate("/org/fao/unredd/charts/highcharts-template.vtl");

		t.merge(context, writer);
		writer.flush();
	}

	private String[] getLabels(List<ValueType> value) {
		String[] ret = new String[value.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = value.get(i).getLabel();
		}
		return ret;
	}

	private String[] getValues(List<ValueType> value) {
		String[] ret = new String[value.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Double.toString(value.get(i).getValue());
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		ChartGenerator chartGenerator = new ChartGenerator(new FileInputStream(
				new File("/home/fergonco/java/nfms/nfms/"
						+ "mosaicStats/src/test/resources/"
						+ "okZonesSHP/output/stats_indicator/result.xml")));
		chartGenerator.generate(new FileWriter(new File("/tmp/a.html")));
	}

}
