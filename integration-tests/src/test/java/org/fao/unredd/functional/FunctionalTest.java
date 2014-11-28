package org.fao.unredd.functional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.postgresql.ds.PGSimpleDataSource;

@Category(IntegrationTest.class)
public class FunctionalTest {

	private static final String CONTEXT_PATH = "portal";
	private static String dbUrl;
	private static String dbUser;
	private static String dbPassword;
	private static String testSchema;

	@BeforeClass
	public static void setupTests() throws IOException {
		Properties testProperties = new Properties();
		InputStream stream = FunctionalTest.class
				.getResourceAsStream("functional-test.properties");
		testProperties.load(stream);
		stream.close();

		dbUrl = testProperties.getProperty("db-url");
		dbUser = testProperties.getProperty("db-user");
		dbPassword = testProperties.getProperty("db-password");
		testSchema = testProperties.getProperty("db-test-schema");
	}

	private Server server;
	private PGSimpleDataSource dataSource;

	@Before
	public void setup() throws Exception {
		// Clean the database
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(dbUrl, dbUser,
				dbPassword);
		Statement statement = connection.createStatement();
		statement.execute("DROP SCHEMA IF EXISTS " + testSchema + " CASCADE");
		statement.execute("CREATE SCHEMA " + testSchema);
		connection.close();

		// Start the server
		server = new Server();

		WebAppContext handler = new WebAppContext();
		handler.setContextPath("/" + CONTEXT_PATH);
		handler.setWar("../demo/target/unredd-portal.war");
		String[] configurations = handler.getConfigurationClasses();
		ArrayList<String> configurationList = new ArrayList<String>();
		Collections.addAll(configurationList, configurations);
		configurationList.add("org.eclipse.jetty.plus.webapp.EnvConfiguration");
		configurationList
				.add("org.eclipse.jetty.plus.webapp.PlusConfiguration");
		handler.setConfigurationClasses(configurationList
				.toArray(new String[configurationList.size()]));

		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { handler, new DefaultHandler() });
		server.setHandler(handlers);

		SocketConnector connector = new SocketConnector();
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });

		dataSource = new PGSimpleDataSource();
		dataSource.setUser(dbUser);
		dataSource.setPassword(dbPassword);
		dataSource.setUrl(dbUrl);
		new Resource(handler, "jdbc/unredd-portal", dataSource);

		server.start();
	}

	@After
	public void stop() throws Exception {
		server.stop();
	}

	@Test
	public void testCommentAndVerify() throws Exception {
		CloseableHttpResponse ret = GET("create-comment", "email",
				"fergonco@gmail.com", "geometry", "POINT(0 1)", "srid",
				"900913", "comment", "boh");

		assertEquals(200, ret.getStatusLine().getStatusCode());

		// Get the verification code from the database
		Object verificationCode = SQL("SELECT verification_code FROM "
				+ testSchema + ".comments ORDER BY date DESC");

		// Verify it
		ret = GET("verify-comment", "verificationCode",
				verificationCode.toString());

		assertEquals(200, ret.getStatusLine().getStatusCode());
	}

	@Test
	public void testCommentWrongEmail() throws Exception {
		CloseableHttpResponse ret = GET("create-comment", "email",
				"wrongaddress", "geometry", "POINT(0 1)", "srid", "900913",
				"comment", "boh");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(500, ret.getStatusLine().getStatusCode());
	}

	@Test
	public void testCommentWrongWKT() throws Exception {
		CloseableHttpResponse ret = GET("create-comment", "email",
				"fergonco@gmail.com", "geometry", "POINT(0, 1)", "srid",
				"900913", "comment", "boh");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(500, ret.getStatusLine().getStatusCode());
	}

	private Object SQL(String sql) throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		assertTrue(resultSet.next());
		Object ret = resultSet.getObject(1);
		resultSet.close();
		statement.close();
		connection.close();
		return ret;
	}

	private CloseableHttpResponse GET(String path, String... parameters)
			throws ClientProtocolException, IOException {
		String url = "http://localhost:8080/" + CONTEXT_PATH + "/" + path + "?";
		for (int i = 0; i < parameters.length; i = i + 2) {
			url += parameters[i] + "="
					+ URLEncoder.encode(parameters[i + 1], "UTF-8") + "&";
		}
		System.out.println("GET " + url);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		return httpClient.execute(get);
	}
}
