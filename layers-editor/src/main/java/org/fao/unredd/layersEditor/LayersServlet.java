package org.fao.unredd.layersEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fao.unredd.portal.Config;

@MultipartConfig
public class LayersServlet extends HttpServlet {
        private static final String APPLICATION_JSON = "application/json";
		private static final String CONFIG = "config";
		private static final String UTF_8 = "UTF-8";
		private static final String LAYERS_JSON = "layers.json";
		private static final String BACKUP_FOLDER = "backup";
		private static final long serialVersionUID = 1L;

		@Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                        throws ServletException, IOException {
        	resp.setContentType(APPLICATION_JSON);
            resp.setCharacterEncoding(UTF_8);
            Config config = (Config) getServletContext().getAttribute(CONFIG);
            String layersTemplate = IOUtils.toString(
				new File(config.getDir(), LAYERS_JSON).toURI(), UTF_8);
    				
            PrintWriter writer = resp.getWriter();
            writer.write(layersTemplate);
        }

		@Override
		protected void doPut(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
			ServletContext ctx = getServletContext();
            Config config = (Config) ctx.getAttribute(CONFIG);
            File backupDir = new File(config.getDir(), BACKUP_FOLDER);
            if (backupDir.exists()) {
            	FileUtils.cleanDirectory(backupDir);
            }
            File layersJSON = new File(config.getDir(), LAYERS_JSON);
            if (layersJSON.exists()) {
                Date date = new Date();
           	 	Timestamp now = new Timestamp(date.getTime());
           	 	File layersJSONBack = new File(backupDir, now.toString().replaceAll("\\s","_").concat("-".concat(LAYERS_JSON)));
           	 	FileUtils.copyFile(layersJSON, layersJSONBack);
                layersJSON.delete();
            } else {
            	// TODO Is this needed?
            	resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not found layers.json file in portal.properties folder!");
            }
            layersJSON.createNewFile();
            BufferedReader reader = req.getReader();
            OutputStream out = new FileOutputStream(layersJSON);
            IOUtils.copy(reader, out, UTF_8);
            
            out.close();
            reader.close();
            
            resp.sendError(HttpServletResponse.SC_OK);
		}
		
}
