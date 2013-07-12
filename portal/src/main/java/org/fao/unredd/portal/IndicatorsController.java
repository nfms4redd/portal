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
package org.fao.unredd.portal;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fao.unredd.charts.ChartGenerator;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Outputs;

public class IndicatorsController {

	private static Logger logger = Logger.getLogger(IndicatorsController.class);

	public static final String PARAM_OBJECT_ID = "objectId";
	public static final String PARAM_LAYER_ID = "layerId";
	public static final String PARAM_INDICATOR_ID = "indicatorId";

	private HttpServletResponse response;
	private LayerFactory layerFactory;

	public IndicatorsController(HttpServletResponse response,
			LayerFactory layerFactory) {
		this.response = response;
		this.layerFactory = layerFactory;
	}

	/**
	 * @param layerId
	 * @throws IOException
	 * @throws NullPointerException
	 *             If layerId is null
	 */
	public void returnIndicators(String layerId) throws IOException,
			NullPointerException {
		if (layerId == null) {
			ApplicationController.ErrorCause.ILLEGAL_ARGUMENT
					.writeError(response);
		} else {
			String answer = "[]";
			if (layerFactory.exists(layerId)) {
				Layer layer = layerFactory.newLayer(layerId);
				Outputs indicators = layer.getOutputs();
				response.setContentType("application/json;charset=UTF-8");
				answer = indicators.toJSON();
			}
			try {
				response.getWriter().print(answer);
				response.flushBuffer();
			} catch (IOException e) {
				logger.error("Error returning the indicators", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

	public void returnIndicator(String objectId, String layerId,
			String indicatorId) throws IOException {
		if (layerId == null || indicatorId == null) {
			ApplicationController.ErrorCause.ILLEGAL_ARGUMENT
					.writeError(response);
		} else {
			try {
				if (layerFactory.exists(layerId)) {
					Layer layer = layerFactory.newLayer(layerId);
					try {
						ChartGenerator chartGenerator = new ChartGenerator(
								new ByteArrayInputStream(layer.getOutput(
										indicatorId).getBytes("UTF-8")));
						response.setContentType(chartGenerator.getContentType());
						chartGenerator.generate(objectId, response.getWriter());
						response.flushBuffer();
					} catch (IOException e) {
						logger.error("Error returning the indicators", e);
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
				} else {
					ApplicationController.ErrorCause.ILLEGAL_ARGUMENT
							.writeError(response);
				}
			} catch (NoSuchIndicatorException e) {
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT
						.writeError(response);
			}
		}
	}
}
