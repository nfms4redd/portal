package org.fao.unredd.layersEditor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.portal.Config;

public class LayersServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                        throws ServletException, IOException {
        	resp.setContentType("application/json");
            resp.setCharacterEncoding("utf8");
            
            Config config = (Config) getServletContext().getAttribute("config");
            String layersTemplate = IOUtils.toString(
				new File(config.getDir(), "layers.json").toURI(), "UTF-8");
    				
            PrintWriter writer = resp.getWriter();
            writer.write(layersTemplate);
	
        }

}
