package org.fao.unredd.portal;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fao.unredd.layers.Output;
import org.fao.unredd.layers.Outputs;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchGeoserverLayerException;
import org.fao.unredd.layers.NoSuchIndicatorException;

public class IndicatorsController {

	private static Logger logger = Logger.getLogger(IndicatorsController.class);

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
			String answer;
			try {
				Layer layer = layerFactory.newLayer(layerId);
				Outputs indicators = layer.getOutputs();
				response.setContentType("application/json;charset=UTF-8");
				answer = indicators.toJSON();
			} catch (NoSuchGeoserverLayerException e1) {
				answer = "[]";
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

	public void returnIndicator(String layerId, String indicatorId)
			throws IOException {
		if (layerId == null || indicatorId == null) {
			ApplicationController.ErrorCause.ILLEGAL_ARGUMENT
					.writeError(response);
		} else {
			try {
				Layer layer = layerFactory.newLayer(layerId);
				Output indicator = layer.getOutput(indicatorId);
				response.setContentType(indicator.getContentType());
				try {
					response.getWriter().print(indicator.getContents());
					response.flushBuffer();
				} catch (IOException e) {
					logger.error("Error returning the indicators", e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} catch (NoSuchIndicatorException e) {
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT
						.writeError(response);
			} catch (NoSuchGeoserverLayerException e) {
				ApplicationController.ErrorCause.ILLEGAL_ARGUMENT
						.writeError(response);
			}
		}
	}
}
