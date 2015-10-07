/**
 * 
 */
package org.fao.unredd.layersEditor;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fao.unredd.portal.Config;
import org.fao.unredd.portal.ConfigFolder;
import org.fao.unredd.portal.DefaultConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author michogarcia
 *
 */
public class LayersEditorTest {
	
	private static final boolean APPEND = true;
	private static final String UTF_8 = "UTF-8";
	private LayersServlet servlet;
	private Config defaultconfig;
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		servlet = new LayersServlet();
		ServletConfig config = mock(ServletConfig.class);
		servlet.init(config);
		defaultconfig = new DefaultConfig(mock(ConfigFolder.class), false);
		ServletContext context = mock(ServletContext.class);
		when(config.getServletContext()).thenReturn(context);
		when(context.getAttribute("config")).thenReturn(defaultconfig);
	}

	/**
	 * Test method for {@link org.fao.unredd.layersEditor.LayersServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@Test
	public void testDoPutHttpServletRequestHttpServletResponse() throws ServletException, IOException {
		
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		
		File tmpDir = testFolder.getRoot();
		
		when(defaultconfig.getDir()).thenReturn(tmpDir);
		
		URL url = this.getClass().getResource("/layers.json");
		File layersJSON = new File(url.getFile());
		
        Date date = new java.util.Date();
   	 	Timestamp now = new Timestamp(date.getTime());
   	 	
   	 	FileUtils.copyFileToDirectory(layersJSON, tmpDir);
   	 	
   	 	File layersJSONCopy = new File(tmpDir, "layers.json");
   	 	FileUtils.writeStringToFile(layersJSONCopy, now.toString(), APPEND);
   	 
		BufferedReader readerLayers = new BufferedReader(new FileReader(layersJSON));
		when(req.getReader()).thenReturn(readerLayers);
		
		servlet.doPut(req, resp);
		
		File backupFolder = new File(tmpDir, "backup");
		InputStream afterPutBackupIs = null;
		Iterator<File> allFilesInBackup = FileUtils.iterateFiles(backupFolder, null, false);
		while (allFilesInBackup.hasNext()) {
			File aFileInFolder = allFilesInBackup.next();
			afterPutBackupIs = new FileInputStream(aFileInFolder);
		}
		InputStream originalLayerJSONIs = new FileInputStream(new File(url.getFile()));
		InputStream afterPutLayersIs = new FileInputStream(new File(tmpDir, "layers.json"));
		boolean equals = IOUtils.contentEquals(afterPutLayersIs, originalLayerJSONIs);
		assertTrue(equals);

		InputStream againOriginalLayerJSONIs = new FileInputStream(new File(url.getFile()));
		equals = IOUtils.contentEquals(afterPutBackupIs, againOriginalLayerJSONIs);
		assertTrue(!equals);
	}
}
