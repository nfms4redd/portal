package org.fao.unredd.portal;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fao.unredd.layers.Indicators;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;

public class IndicatorsController {

	private static Logger logger = Logger.getLogger(IndicatorsController.class);

	public static final String PARAM_LAYER_ID = "layerId";

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
			throw new NullPointerException("The parameter is mandatory: "
					+ PARAM_LAYER_ID);
		}
		Layer layer = layerFactory.newLayer(layerId);
		Indicators indicators = layer.getIndicators();
		response.setContentType("application/json;charset=UTF-8");
		try {
			response.getWriter().print(indicators.toJSON());
			response.flushBuffer();
		} catch (IOException e) {
			logger.error("Error returning the indicators", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
