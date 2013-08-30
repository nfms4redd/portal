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

import org.apache.log4j.Logger;
import org.fao.unredd.portal.ApplicationController.ErrorCause;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.unredd.generated.tables.pojos.Feedbacks;
import org.jooq.unredd.generated.tables.records.FeedbacksRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

import static org.jooq.unredd.generated.Routines.stGeomfromtext1;
import static org.jooq.unredd.generated.tables.Feedbacks.FEEDBACKS;


//import net.tanesha.recaptcha.ReCaptcha;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    DSLContext dsl;

    @Autowired
    PlatformTransactionManager transactionManager;

    //@Autowired
    //net.tanesha.recaptcha.ReCaptchaImpl reCaptcha;

    private static Logger logger = Logger.getLogger(FeedbackController.class);

//	static final String PARAM_RECAPTCHA_RESPONSE = "recaptcha_response";
//	static final String PARAM_RECAPTCHA_CHALLENGE = "recaptcha_challenge";
	static final String PARAM_USER_MAIL  = "user_mail";
	static final String PARAM_USER_NAME  = "user_name";
	static final String PARAM_LAYER_NAME = "layer_name";
    static final String PARAM_TEXT       = "text";
    static final String PARAM_GEO        = "geo";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public void getFeedbacks() {
        // TODO
        Result<Record> result = dsl.select().from("feedbacks").fetch();

        for (Record r : result) {
            int id  = r.getValue(FEEDBACKS.ID);
            String t = r.getValue(FEEDBACKS.TEXT);

            System.out.println("ID: " + id + " text: " + t); // DEBUG
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getFeedback(@PathVariable int id) {
        Result<Record> result = dsl.select().from(FEEDBACKS).where(FEEDBACKS.ID.equal(id)).fetch();
        if (result.size() != 1)
            return new ResponseEntity<String>(ErrorCause.ILLEGAL_ARGUMENT.getJson(), HttpStatus.NOT_FOUND);

        return new ResponseEntity<String>(result.formatJSON(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> postFeedback(
            @RequestParam(value = PARAM_LAYER_NAME) String layerName,
            @RequestParam(value = PARAM_USER_NAME)  String userName,
            @RequestParam(value = PARAM_USER_MAIL)  String userMail,
            @RequestParam(value = PARAM_TEXT)       String text,
            @RequestParam(value = PARAM_GEO)        String geo // Geometry (WKT)
    ) {
        try {
            // Create a new POJO instance
            Feedbacks fb = new Feedbacks();
            fb.setLayerName(layerName);
            fb.setUserName(userName);
            fb.setUserMail(userMail);
            fb.setText(text);
            fb.setGeom(stGeomfromtext1(geo));

            // Load a jOOQ-generated record from your POJO
            FeedbacksRecord fbRecord = dsl.newRecord(FEEDBACKS, fb);

            // Insert it
            fbRecord.store();

//          dsl.insertInto(FEEDBACKS, (Field<Object>) fb);
//          dsl.insertInto(FEEDBACKS,
//                  FEEDBACKS.ID, FEEDBACKS.USER_NAME, FEEDBACKS.USER_MAIL, FEEDBACKS.TEXT, FEEDBACKS.GEO)
//                  .values(100L, userName, userMail, text, geo).execute();

            return new ResponseEntity<String>(ErrorCause.FEEDBACK_OK.getJson(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(ErrorCause.ILLEGAL_ARGUMENT.getJson(), HttpStatus.BAD_REQUEST);
        }
    }


//    @RequestMapping(method = RequestMethod.POST)
//	public void postFeedback_(HttpServletRequest request,
//                             HttpServletResponse response) throws IOException {
//		try {
//            // Get HTTP params
//			String layerName = getNonNullParameter(request, PARAM_LAYER_NAME);
//			String userName  = getNonNullParameter(request, PARAM_USER_NAME);
//			String userMail  = getNonNullParameter(request, PARAM_USER_MAIL);
//			String geo       = getRequestBodyAsString(request);
//
////			String captchaChallenge = getNonNullParameter(PARAM_RECAPTCHA_CHALLENGE);
////			String captchaResponse = getNonNullParameter(PARAM_RECAPTCHA_RESPONSE);
////			if (!recaptcha.checkAnswer(request.getRemoteAddr(),
////					captchaChallenge, captchaResponse).isValid()) {
////				throw new CaptchaFailedException();
////			}
//
//			// Test syntax: Convert to JSON and back to String.
//			//
//			// .toJSON(polygon).toString(2);
//
////          System.out.println(dsl.selectOne().fetch());
////            Result<Record> result = dsl.select().from(FEEDBACKS).fetch();
//
//            dsl.insertInto(FEEDBACKS,
//                    FEEDBACKS.ID, FEEDBACKS.USER_NAME, FEEDBACKS.USER_MAIL, FEEDBACKS.GEO)
//                    .values(100L, userName, userMail, "this is the geo!").execute();
//
//            Result<Record> result = dsl.select().from("feedbacks").fetch();
//
//            System.out.println("size = " + result.size()); // DEBUG
//
//            for (Record r : result) {
//                Long id     = r.getValue(FEEDBACKS.ID);
//                String text = r.getValue(FEEDBACKS.TEXT);
//
//                System.out.println("ID: " + id + " text: " + text);
//            }
//
////            EntityManagerFactory emf = Persistence
////					.createEntityManagerFactory("layers");
////			EntityManager entityManager = emf.createEntityManager();
////			EntityTransaction transaction = entityManager.getTransaction();
////			transaction.begin();
////			Feedback feedback = new Feedback();
////			feedback.setLayerName(layerName);
////			feedback.setUserName(userName);
////			feedback.setUserEMail(userMail);
////			entityManager.persist(feedback);
////			transaction.commit();
////			entityManager.close();
////			emf.close();
////
////			response.getWriter().write(ErrorCause.FEEDBACK_OK.getJson());
//		} catch (IllegalArgumentException e) {
//			ErrorCause.ILLEGAL_ARGUMENT.writeError(response);
////		} catch (CaptchaFailedException e) {
////			ErrorCause.UNAUTHORIZED.writeError(response);
//		} catch (JSONException e) {
//			logger.error(e);
//			ErrorCause.SYNTAX_ERROR.writeError(response);
//		}
//
//	}

	private String getNonNullParameter(HttpServletRequest request,
                                       String paramLayerName) {
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

	private String getRequestBodyAsString(HttpServletRequest request) throws IOException {
		StringBuffer body = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null) {
			body.append(line);
		}

		return body.toString();
	}

}