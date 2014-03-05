package org.fao.unredd.portal;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.charts.ChartGenerator;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchIndicatorException;

public class IndicatorDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String layerId = req.getParameter("layerId");
		String indicatorId = req.getParameter("indicatorId");
		String objectId = req.getParameter("objectId");
		if (layerId == null || indicatorId == null) {
			throw new StatusServletException(400,
					"layerId and indicatorId parameters are mandatory");
		} else {
			try {
				LayerFactory layerFactory = (LayerFactory) getServletContext()
						.getAttribute("layer-factory");
				if (layerFactory.exists(layerId)) {
					Layer layer = layerFactory.newLayer(layerId);
					ChartGenerator chartGenerator = new ChartGenerator(
							new ByteArrayInputStream(layer.getOutput(
									indicatorId).getBytes("UTF-8")));
					resp.setContentType(chartGenerator.getContentType());
					chartGenerator.generate(objectId, resp.getWriter());
					resp.flushBuffer();
				} else {
					throw new StatusServletException(400, "The layer "
							+ layerId + " does not exist");
				}
			} catch (NoSuchIndicatorException e) {
				throw new StatusServletException(400, "The indicator "
						+ indicatorId + " does not exist");
			}
		}
	}

}
