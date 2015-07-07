package org.fao.unredd.functional;

import static junit.framework.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

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
import org.fao.unredd.functional.feedback.FeedbackTest;
import org.fao.unredd.functional.stats.StatsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.postgresql.ds.PGSimpleDataSource;

public class AbstractIntegrationTest {

	private static final String CONTEXT_PATH = "portal";
	private static String dbUrl;
	private static String dbUser;
	private static String dbPassword;
	protected static String testSchema;

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

		// Install data tables in integration_tests
		SQLExecute(getScript("redd_feedback.sql").replaceAll("redd_feedback",
				"integration_tests.redd_feedback"));
		SQLExecute(getScript("redd_stats_metadata.sql").replaceAll(
				"CREATE TABLE ", "CREATE TABLE integration_tests."));
		// Install functions in public
		executeDelimitedScript("redd_stats_calculator.sql");
		SQLExecute(getScript("redd_stats_fajas.sql").replaceAll(
				"redd_stats_fajas", "integration_tests.redd_stats_fajas"));

		// Install test data
		executeLines("data.sql", "schemaName", testSchema);
	}

	@After
	public void stop() throws Exception {
		server.stop();
	}

	protected Object SQLQuery(String sql) throws SQLException {
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

	protected void SQLExecute(String sql) throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute(sql);
		statement.close();
		connection.close();
	}

	/**
	 * Executes statements delimited by --- in the specified resource.
	 */
	private void executeDelimitedScript(String resourceName)
			throws IOException, SQLException {
		String script = getScript(resourceName);
		String[] lines = script.split(Pattern.quote("---"));
		for (String line : lines) {
			executeSQLStatement(line);
		}
	}

	protected String getScript(String resourceName) throws IOException {
		InputStream stream = StatsTest.class.getResourceAsStream(resourceName);
		String script = IOUtils.toString(stream);
		stream.close();
		return script;
	}

	private void executeSQLStatement(String script, String... params)
			throws SQLException {
		for (int i = 0; i < params.length; i += 2) {
			script = script.replaceAll(Pattern.quote("$" + params[i]),
					params[i + 1]);
		}
		SQLExecute(script);
	}

	protected void executeLines(String resourceName, String... params)
			throws IOException, SQLException {
		InputStream stream = StatsTest.class.getResourceAsStream(resourceName);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		String line = null;
		while ((line = reader.readLine()) != null) {
			executeSQLStatement(line, params);
		}

		stream.close();
	}

	protected CloseableHttpResponse GET(String path, String... parameters)
			throws ClientProtocolException, IOException {
		String url = "http://localhost:8080/" + CONTEXT_PATH + "/" + path + "?";
		for (int i = 0; i < parameters.length; i = i + 2) {
			url += parameters[i] + "="
					+ URLEncoder.encode(parameters[i + 1], "UTF-8") + "&";
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		return httpClient.execute(get);
	}

	protected CloseableHttpResponse POST(String path, String... parameters)
			throws ClientProtocolException, IOException {
		String url = "http://localhost:8080/" + CONTEXT_PATH + "/" + path;
		ArrayList<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		for (int i = 0; i < parameters.length; i = i + 2) {
			parameterList.add(new BasicNameValuePair(parameters[i],
					parameters[i + 1]));
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost put = new HttpPost(url);
		put.setEntity(new UrlEncodedFormEntity(parameterList));
		return httpClient.execute(put);
	}
}
