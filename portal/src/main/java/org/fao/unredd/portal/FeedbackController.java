package org.fao.unredd.portal;

import java.io.BufferedReader;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;
import net.tanesha.recaptcha.ReCaptcha;

import org.apache.log4j.Logger;
import org.fao.unredd.model.Feedback;
import org.fao.unredd.portal.ApplicationController.ErrorCause;

public class FeedbackController {

	private static Logger logger = Logger.getLogger(FeedbackController.class);

	static final String PARAM_RECAPTCHA_RESPONSE = "recaptcha_response";
	static final String PARAM_RECAPTCHA_CHALLENGE = "recaptcha_challenge";
	static final String PARAM_USER_MAIL = "UserMail";
	static final String PARAM_USER_NAME = "UserName";
	static final String PARAM_LAYER_NAME = "LayerName";

	private HttpServletRequest request;
	private HttpServletResponse response;
	private ReCaptcha recaptcha;

	public FeedbackController(HttpServletRequest request,
			HttpServletResponse response, ReCaptcha reCaptcha) {
		this.request = request;
		this.response = response;
		this.recaptcha = reCaptcha;
	}

	public void postFeedback() throws IOException {
		try {
			String layerName = getNonNullParameter(PARAM_LAYER_NAME);
			String userName = getNonNullParameter(PARAM_USER_NAME);
			String userMail = getNonNullParameter(PARAM_USER_MAIL);
			String polygon = getRequestBodyAsString(request, response);
			String captchaChallenge = getNonNullParameter(PARAM_RECAPTCHA_CHALLENGE);
			String captchaResponse = getNonNullParameter(PARAM_RECAPTCHA_RESPONSE);
			if (!recaptcha.checkAnswer(request.getRemoteAddr(),
					captchaChallenge, captchaResponse).isValid()) {
				throw new CaptchaFailedException();
			}

			// Test syntax: Convert to JSON and back to String.
			JSONSerializer.toJSON(polygon).toString(2);

			EntityManagerFactory emf = Persistence
					.createEntityManagerFactory("layers");
			EntityManager entityManager = emf.createEntityManager();
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			Feedback feedback = new Feedback();
			feedback.setLayerName(layerName);
			feedback.setUserName(userName);
			feedback.setUserEMail(userMail);
			entityManager.persist(feedback);
			transaction.commit();
			entityManager.close();
			emf.close();

			response.getWriter().write(ErrorCause.FEEDBACK_OK.getJson());
		} catch (IllegalArgumentException e) {
			ErrorCause.ILLEGAL_ARGUMENT.writeError(response);
		} catch (CaptchaFailedException e) {
			ErrorCause.UNAUTHORIZED.writeError(response);
		} catch (JSONException e) {
			logger.error(e);
			ErrorCause.SYNTAX_ERROR.writeError(response);
		}

	}

	private String getNonNullParameter(String paramLayerName) {
		String ret = request.getParameter(paramLayerName);
		if (ret == null) {
			throw new IllegalArgumentException(paramLayerName
					+ " should not be null");
		} else if (ret.trim().length() == 0) {
			throw new IllegalArgumentException(paramLayerName
					+ " should not be empty");
		}
		return ret;
	}

	private String getRequestBodyAsString(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		StringBuffer body = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null) {
			body.append(line);
		}

		return body.toString();
	}

}