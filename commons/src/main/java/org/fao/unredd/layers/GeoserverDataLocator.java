package org.fao.unredd.layers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.jndi.toolkit.url.Uri;

public class GeoserverDataLocator implements DataLocator {

	private static final String URL_XPATH = "/dataStore/connectionParameters/entry[@key='url']";
	private File geoserverDataDir;

	public GeoserverDataLocator(File geoserverDataDir) {
		this.geoserverDataDir = geoserverDataDir;
	}

	@Override
	public Location locate(final Layer layer) throws CannotFindLayerException,
			IOException {
		final File workspacesFolder = new File(geoserverDataDir, "workspaces");
		File layerFolder = find(workspacesFolder, new FileFilter() {

			@Override
			public boolean accept(File file) {
				File workspace = file.getParentFile().getParentFile();
				return file.isDirectory()
						&& file.getName().equals(layer.getName())
						&& workspace.getName().equals(layer.getWorkspace())
						&& workspace.getParentFile().equals(workspacesFolder);
			}

		});
		if (layerFolder == null) {
			throw new CannotFindLayerException(layer.getQualifiedName());
		}
		File coverageStore = new File(layerFolder.getParentFile(),
				"coveragestore.xml");
		File dataStore = new File(layerFolder.getParentFile(), "datastore.xml");
		try {
			if (coverageStore.exists()) {
				String url = xpath(coverageStore, "/coverageStore/url");
				File file = getFile(url);
				return new FileLocation(file);
			} else if (dataStore.exists()) {
				String type = xpath(dataStore, "/dataStore/type");
				if (type.equals("Shapefile")) {
					String url = xpath(dataStore, URL_XPATH);
					File file = getFile(url);
					return new FileLocation(file);
				} else if (type
						.equals("Directory of spatial files (shapefiles)")) {
					String url = xpath(dataStore, URL_XPATH);
					String fileName = xpath(new File(layerFolder,
							"featuretype.xml"), "/featureType/nativeName");
					fileName = fileName + ".shp";
					File file = new File(getFile(url), fileName);
					return new FileLocation(file);
				} else {
					throw new UnsupportedOperationException(
							"Unsupported type: " + type);
				}
			} else {
				throw new IllegalArgumentException(
						"Can only locate vector or raster layers");
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException("bug", e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("bug", e);
		} catch (SAXException e) {
			throw new IOException("Cannot parse the geoserver data files", e);
		}
	}

	private File getFile(String url) throws MalformedURLException {
		String path = new Uri(url).getPath();
		File file = new File(path);
		if (!file.isAbsolute()) {
			file = new File(geoserverDataDir, path);
		}
		return file;
	}

	private File find(File folder, FileFilter fileFilter) {
		if (fileFilter.accept(folder)) {
			return folder;
		} else if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				File find = find(file, fileFilter);
				if (find != null) {
					return find;
				}
			}
		}
		return null;
	}

	private String xpath(File file, String expression)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		Document doc = builder.parse(bis);
		bis.close();
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(expression);
		return expr.evaluate(doc);
	}
}
