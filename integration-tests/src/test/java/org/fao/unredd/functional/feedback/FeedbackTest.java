package org.fao.unredd.functional.feedback;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.fao.unredd.functional.AbstractIntegrationTest;
import org.fao.unredd.functional.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FeedbackTest extends AbstractIntegrationTest {

	@Test
	public void testCommentAndVerify() throws Exception {
		String email = "fergonco@gmail.com";
		String geometry = "POINT(0 1)";
		String comment = "boh";
		String layerName = "classification";
		String layerDate = "1/1/2008";
		CloseableHttpResponse ret = POST("create-comment", "email", email,
				"geometry", geometry, "comment", comment, "layerName",
				layerName, "date", layerDate);

		assertEquals(200, ret.getStatusLine().getStatusCode());

		// Get the verification code from the database
		String verificationCode = SQLQuery(
				"SELECT verification_code FROM " + testSchema
						+ ".redd_feedback ORDER BY date DESC").toString();

		// Check the insert
		assertEquals(email, SQLQuery("SELECT email FROM " + testSchema
				+ ".redd_feedback WHERE verification_code='" + verificationCode
				+ "'"));
		assertEquals(geometry, SQLQuery("SELECT ST_AsText(geometry) FROM "
				+ testSchema + ".redd_feedback WHERE verification_code='"
				+ verificationCode + "'"));
		assertEquals(comment, SQLQuery("SELECT comment FROM " + testSchema
				+ ".redd_feedback WHERE verification_code='" + verificationCode
				+ "'"));
		assertEquals(layerName, SQLQuery("SELECT layer_name FROM " + testSchema
				+ ".redd_feedback WHERE verification_code='" + verificationCode
				+ "'"));
		assertEquals(layerDate, SQLQuery("SELECT layer_date FROM " + testSchema
				+ ".redd_feedback WHERE verification_code='" + verificationCode
				+ "'"));

		// Verify it the comment
		ret = GET("verify-comment", "verificationCode", verificationCode);
		assertEquals(200, ret.getStatusLine().getStatusCode());

		// Check cannot validate twice
		ret = GET("verify-comment", "verificationCode", verificationCode);
		assertEquals(404, ret.getStatusLine().getStatusCode());

		// Check validation has not been notified to author
		Long notifiedCount = (Long) SQLQuery("SELECT count(*) FROM "
				+ testSchema + ".redd_feedback WHERE state=3");
		assertEquals(0, notifiedCount.longValue());

		// Validate the entry and wait (more than the notification delay)
		SQLExecute("UPDATE " + testSchema
				+ ".redd_feedback SET state=2 WHERE verification_code='"
				+ verificationCode + "'");
		synchronized (this) {
			wait(4000);
		}

		// Check the entry has been marked as "notified"
		notifiedCount = (Long) SQLQuery("SELECT count(*) FROM " + testSchema
				+ ".redd_feedback WHERE state=3");
		assertEquals(1, notifiedCount.longValue());
	}

	@Test
	public void testCommentWrongEmail() throws Exception {
		CloseableHttpResponse ret = POST("create-comment", "email",
				"wrongaddress", "geometry", "POINT(0 1)", "comment", "boh",
				"layerName", "classification", "date", "1/1/2008");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(500, ret.getStatusLine().getStatusCode());
	}

	@Test
	public void testCommentWrongWKT() throws Exception {
		CloseableHttpResponse ret = POST("create-comment", "email",
				"onuredd@gmail.com", "geometry", "POINT(0, 1)", "comment",
				"boh", "layerName", "classification", "date", "1/1/2008");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(500, ret.getStatusLine().getStatusCode());
	}

	@Test
	public void testVerifyI18n() throws Exception {
		CloseableHttpResponse en = GET("verify-comment", "verificationCode",
				"1", "lang", "en");
		CloseableHttpResponse es = GET("verify-comment", "verificationCode",
				"1", "lang", "es");
		assertFalse(en.equals(es));
	}

	@Test
	public void testMissingParameter() throws Exception {
		CloseableHttpResponse ret = POST("create-comment", "email",
				"onuredd@gmail.com", "geometry", "POINT(0, 1)", "layerName",
				"classification", "date", "1/1/2008");

		System.out.println(IOUtils.toString(ret.getEntity().getContent()));

		assertEquals(400, ret.getStatusLine().getStatusCode());
	}

}
