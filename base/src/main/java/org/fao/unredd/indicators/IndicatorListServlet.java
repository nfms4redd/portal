package org.fao.unredd.indicators;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.Outputs;
import org.fao.unredd.portal.StatusServletException;

public class IndicatorListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String layerId = req.getParameter("layerId");
		if (layerId == null) {
			throw new StatusServletException(400,
					"layerId parameter is mandatory");
		} else {
			String answer = "[]";
			LayerFactory layerFactory = (LayerFactory) getServletContext()
					.getAttribute("layer-factory");
			if (layerFactory.exists(layerId)) {
				Layer layer = layerFactory.newLayer(layerId);
				Outputs indicators = layer.getOutputs();
				answer = indicators.toJSON();
			}
			try {
				resp.setContentType("application/json");
				resp.setCharacterEncoding("utf8");
				resp.getWriter().print(answer);
			} catch (IOException e) {
				throw new ServletException(
						"Could not obtain indicators for layer: " + layerId, e);
			}
		}

	}

}
