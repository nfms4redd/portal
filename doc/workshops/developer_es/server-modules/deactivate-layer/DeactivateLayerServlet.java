package org.fao.unredd.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

public class DeactivateLayerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Config config = (Config) getServletContext().getAttribute("config");

		String layerId = req.getParameter("layerId");

		File layerFile = config.getLayersFile();
		FileInputStream is = new FileInputStream(layerFile);
		String layersContent = IOUtils.toString(is);
		is.close();

		JSONObject obj = (JSONObject) JSONSerializer.toJSON(layersContent);
		JSONArray portalLayers = obj.getJSONArray("portalLayers");
		for (int i = 0; i < portalLayers.size(); i++) {
			JSONObject portalLayer = portalLayers.getJSONObject(i);
			if (portalLayer.getString("id").equals(layerId)) {
				portalLayer.element("active", false);
			}
		}

		FileOutputStream os = new FileOutputStream(layerFile);
		IOUtils.write(obj.toString(), os);
		os.close();
	}
}
