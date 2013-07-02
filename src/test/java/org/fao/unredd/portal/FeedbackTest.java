/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.portal;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class FeedbackTest {

	private static ReCaptchaResponse INVALID_CAPTCHA;
	private static ReCaptchaResponse VALID_CAPTCHA;

	private ReCaptcha captcha;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeClass
	public static void setupCaptchaResponses() {
		INVALID_CAPTCHA = mock(ReCaptchaResponse.class);
		when(INVALID_CAPTCHA.isValid()).thenReturn(false);

		VALID_CAPTCHA = mock(ReCaptchaResponse.class);
		when(VALID_CAPTCHA.isValid()).thenReturn(true);
	}

	@Before
	public void setupCommon() throws IOException {
		captcha = mock(ReCaptcha.class);
		when(captcha.checkAnswer(anyString(), anyString(), anyString()))
				.thenReturn(VALID_CAPTCHA);
		request = mockRequest("thelayername", "username", "user@mail.com",
				"challenge", "response", "{}");
		response = mockResponse();
	}

	private HttpServletRequest mockRequest(String layerName, String userName,
			String userMail, String captchaChallenge, String captchaResponse,
			String body) throws IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(FeedbackController.PARAM_LAYER_NAME))
				.thenReturn(layerName);
		when(request.getParameter(FeedbackController.PARAM_USER_NAME))
				.thenReturn(userName);
		when(request.getParameter(FeedbackController.PARAM_USER_MAIL))
				.thenReturn(userMail);
		when(request.getParameter(FeedbackController.PARAM_RECAPTCHA_CHALLENGE))
				.thenReturn(captchaChallenge);
		when(request.getParameter(FeedbackController.PARAM_RECAPTCHA_RESPONSE))
				.thenReturn(captchaResponse);
		BufferedReader reader = mock(BufferedReader.class);
		when(reader.readLine()).thenReturn(body).thenReturn(null);
		when(request.getReader()).thenReturn(reader);
		return request;
	}

	private HttpServletResponse mockResponse() throws IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(mock(PrintWriter.class));
		return response;
	}

	@Test
	public void testOk() throws Exception {
		FeedbackController feedback = new FeedbackController(request, response,
				captcha);

		feedback.postFeedback();

		verify(response, never()).setStatus(anyInt());
	}

	@Test
	public void testNullParameters() throws Exception {
		testValidationFail(
				mockRequest(null, "username", "user@mail.com", "challenge",
						"response", "{}"), mockResponse(), captcha);
		testValidationFail(
				mockRequest("thelayername", null, "user@mail.com", "challenge",
						"response", "{}"), mockResponse(), captcha);
		testValidationFail(
				mockRequest("thelayername", "username", null, "challenge",
						"response", "{}"), mockResponse(), captcha);
		testValidationFail(
				mockRequest("thelayername", "username", "user@mail.com", null,
						"response", "{}"), mockResponse(), captcha);
		testValidationFail(
				mockRequest("thelayername", "username", "user@mail.com",
						"challenge", null, "{}"), mockResponse(), captcha);
	}

	private void testValidationFail(HttpServletRequest req,
			HttpServletResponse res, ReCaptcha cap) throws IOException {
		FeedbackController feedback = new FeedbackController(req, res, cap);
		feedback.postFeedback();
		verify(res, times(1)).setStatus(anyInt());
	}

	@Ignore
	@Test
	public void testInvalidParameters() throws Exception {
		fail();
	}

	@Ignore
	@Test
	public void testInvalidPolygon() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		BufferedReader reader = mock(BufferedReader.class);
		fail("try actual invalid json");
		when(reader.readLine()).thenReturn(null);
		when(request.getReader()).thenReturn(reader);
		FeedbackController feedback = new FeedbackController(request, response,
				captcha);

		feedback.postFeedback();

		verify(response, times(1)).setStatus(anyInt());
	}

	@Test
	public void testCaptchaFailed() throws Exception {
		ReCaptcha captcha = mock(ReCaptcha.class);
		when(captcha.checkAnswer(anyString(), anyString(), anyString()))
				.thenReturn(INVALID_CAPTCHA);
		FeedbackController feedback = new FeedbackController(request, response,
				captcha);

		feedback.postFeedback();

		verify(response, times(1)).setStatus(anyInt());

	}

	@Ignore
	@Test
	public void testPersistenceException() throws Exception {
		fail();
	}

}
