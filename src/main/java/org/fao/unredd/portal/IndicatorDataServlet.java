package org.fao.unredd.portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Output;

public class IndicatorDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String layerId = req.getParameter("layerId");
		String indicatorId = req.getParameter("indicatorId");
		String objectId = req.getParameter("objectId");
		if (layerId == null || indicatorId == null) {
			throw new StatusServletExceptionImpl(400,
					"layerId and indicatorId parameters are mandatory");
		} else {
			try {
				LayerFactory layerFactory = (LayerFactory) getServletContext()
						.getAttribute("layer-factory");
				if (layerFactory.exists(layerId)) {
					Layer layer = layerFactory.newLayer(layerId);
					Output output = layer.getOutput(indicatorId, objectId);
					resp.setContentType(output.getContentType());
					output.writeOutput(resp.getOutputStream());
				} else {
					throw new StatusServletExceptionImpl(400, "The layer "
							+ layerId + " does not exist");
				}
			} catch (NoSuchIndicatorException e) {
				throw new StatusServletExceptionImpl(400, "The indicator "
						+ indicatorId + " does not exist");
			}
		}
	}

}
