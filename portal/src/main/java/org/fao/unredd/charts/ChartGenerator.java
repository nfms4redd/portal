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
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.fao.unredd.charts.generated.DataType;
import org.fao.unredd.charts.generated.StatisticsChartInput;

/**
 * 
 */
public class ChartGenerator {

	private StatisticsChartInput inputData;

	public ChartGenerator(InputStream chartInput) {
		inputData = JAXB.unmarshal(chartInput, StatisticsChartInput.class);
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
		context.put("dates", inputData.getLabels().getLabel().iterator());
		context.put("y-label", nullToEmptyString(inputData.getYLabel()));
		context.put("units", nullToEmptyString(inputData.getUnits()));
		context.put("tooltipDecimals",
				nullToEmptyString(inputData.getTooltipDecimals()));
		context.put("data", getValues(objectId, inputData.getData()));
		context.put("hover", nullToEmptyString(inputData.getHover()));
		context.put("footer", nullToEmptyString(inputData.getFooter()));

		Template t = engine
				.getTemplate("/org/fao/unredd/charts/highcharts-template.vtl");

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
