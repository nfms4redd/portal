package org.fao.unredd.functional.feedback;

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
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fao.unredd.functional.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.postgresql.ds.PGSimpleDataSource;

@Category(IntegrationTest.class)
public class FeedbackTest {

	private static final String CONTEXT_PATH = "portal";
	private static String dbUrl;
	private static String dbUser;
	private static String dbPassword;
	private static String testSchema;

	@BeforeClass
	public static void setupTests() throws IOException {
		Properties testProperties = new Properties();
		InputStream stream = FeedbackTest.class
				.getResourceAsStream("/org/fao/unredd/functional/functional-test.properties");
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
		CloseableHttpResponse ret = POST("create-comment", "email",
				"onuredd@gmail.com", "geometry", "POINT(0 1)", "comment", "boh");

		assertEquals(200, ret.getStatusLine().getStatusCode());

		// Get the verification code from the database
		String verificationCode = SQLQuery(
				"SELECT verification_code FROM " + testSchema
						+ ".comments ORDER BY date DESC").toString();

		// Verify it
		ret = GET("verify-comment", "verificationCode", verificationCode);

		assertEquals(200, ret.getStatusLine().getStatusCode());

		// Check validation has not been notified to author
		Long notifiedCount = (Long) SQLQuery("SELECT count(*) FROM "
				+ testSchema + ".comments WHERE state=3");
		assertEquals(0, notifiedCount.longValue());

		// Validate the entry and wait (more than the notification delay)
		SQLExecute("UPDATE " + testSchema
				+ ".comments SET state=2 WHERE verification_code='"
				+ verificationCode + "'");
		synchronized (this) {
			wait(3000);
		}

		// Check the entry has been marked as "notified"
		notifiedCount = (Long) SQLQuery("SELECT count(*) FROM " + testSchema
				+ ".comments WHERE state=3");
		assertEquals(1, notifiedCount.longValue());
	}

	@Test
	public void testCommentWrongEmail() throws Exception {
		CloseableHttpResponse ret = POST("create-comment", "email",
				"wrongaddress", "geometry", "POINT(0 1)", "comment", "boh");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(500, ret.getStatusLine().getStatusCode());
	}

	@Test
	public void testCommentWrongWKT() throws Exception {
		CloseableHttpResponse ret = POST("create-comment", "email",
				"onuredd@gmail.com", "geometry", "POINT(0, 1)", "comment",
				"boh");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(500, ret.getStatusLine().getStatusCode());
	}

	@Test
	public void testMissingParameter() throws Exception {
		CloseableHttpResponse ret = POST("create-comment", "email",
				"onuredd@gmail.com", "geometry", "POINT(0, 1)");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(400, ret.getStatusLine().getStatusCode());
	}

	private Object SQLQuery(String sql) throws SQLException {
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

	private void SQLExecute(String sql) throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute(sql);
		statement.close();
		connection.close();
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

	private CloseableHttpResponse POST(String path, String... parameters)
			throws ClientProtocolException, IOException {
		String url = "http://localhost:8080/" + CONTEXT_PATH + "/" + path;
		ArrayList<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		for (int i = 0; i < parameters.length; i = i + 2) {
			parameterList.add(new BasicNameValuePair(parameters[i],
					parameters[i + 1]));
		}
		System.out.println("PUT " + url + " + " + parameterList);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost put = new HttpPost(url);
		put.setEntity(new UrlEncodedFormEntity(parameterList));
		return httpClient.execute(put);
	}
}
