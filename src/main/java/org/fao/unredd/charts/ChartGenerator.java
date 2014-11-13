/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.charts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.fao.unredd.charts.generated.DataType;
import org.fao.unredd.layers.Output;

/**
 * 
 * @destructor manureta
 */
public class ChartGenerator {

	private Output inputData;

	/*
	 * public ChartGenerator(String string) { // inputData =
	 * JAXB.unmarshal(chartInput, StatisticsChartInput.class); inputData = new
	 * Output(string; }
	 */
	public ChartGenerator(Output output) {
		// inputData = JAXB.unmarshal(chartInput, StatisticsChartInput.class);
		inputData = output;
	}

	public ChartGenerator(FileInputStream fileInputStream) {
		// TODO Auto-generated constructor stub
	}

	public void generate(String objectId, Writer writer) throws IOException {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty("resource.loader", "class");
		engine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		engine.init();
		VelocityContext context = new VelocityContext();

		context.put("title", nullToEmptyString(inputData.getTitle()));
		context.put("subtitle", nullToEmptyString(inputData.getSubtitle()));
		context.put("y-label", nullToEmptyString(inputData.getY_label()));
		context.put("units", nullToEmptyString(inputData.getUnits()));
		context.put("tooltipDecimals",
				nullToEmptyString(inputData.getTooltipsdecimals()));

		context.put("datas", inputData.getData(objectId).iterator());
		context.put("series", inputData.getSeries(objectId));
		context.put("dates", inputData.getLabels(objectId).iterator());

		context.put("hover", nullToEmptyString(inputData.getHover()));
		context.put("footer", nullToEmptyString(inputData.getFooter()));

		String template = "";
		if (inputData.getGraphicType().equals("3d")) {
			template = "/org/fao/unredd/charts/highcharts-3D-template.vtl";
		} else {
			template = "/org/fao/unredd/charts/highcharts-template.vtl";
		}
		Template t = engine.getTemplate(template);
		t.merge(context, writer);
		writer.flush();
	}

	private Object nullToEmptyString(Object value) {
		return value == null ? "" : value;
	}

	private Iterator<Double> getValues(String id, List<DataType> data) {
		for (DataType dataType : data) {
			if (dataType.getZoneId().equals(id)) {
				return dataType.getValue().iterator();
			}
		}

		return null;
	}

	public static void main(String[] args) throws Exception {
		ChartGenerator chartGenerator = new ChartGenerator(
				new FileInputStream(
						new File(
								"/home/fergonco/java/nfms/nfms/"
										+ "portal/testlayer/output/stats-indicator_unredd_temporalMosaic/result.xml")));
		chartGenerator.generate("2", new FileWriter(new File("/tmp/a.html")));
	}

	public String getContentType() {
		return "text/html;charset=UTF-8";
	}

}
