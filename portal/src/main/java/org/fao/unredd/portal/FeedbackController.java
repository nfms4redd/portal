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

import java.sql.Date;
import java.sql.Timestamp;

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
    static final String PARAM_LAYER_DATE = "layer_date";
    static final String PARAM_TEXT       = "text";
    static final String PARAM_GEO        = "geo";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getFeedbacks() {
        Result<Record> result = dsl.select().from(FEEDBACKS).fetch();
        return new ResponseEntity<String>(result.formatJSON(), HttpStatus.OK);
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
            @RequestParam(value = PARAM_LAYER_DATE, required = false) Long layerDate,
            @RequestParam(value = PARAM_USER_NAME)  String userName,
            @RequestParam(value = PARAM_USER_MAIL)  String userMail,
            @RequestParam(value = PARAM_TEXT)       String text,
            @RequestParam(value = PARAM_GEO)        String geo // Geometry (WKT)
    ) {
        try {
            // Create a new POJO instance
            Feedbacks fb = new Feedbacks();
            fb.setLayerName(layerName);
            if (layerDate != null)
                fb.setLayerDate(new Date(layerDate));
            fb.setUserName(userName);
            fb.setUserMail(userMail);
            fb.setText(text);
            fb.setGeom(stGeomfromtext1(geo));

            // insert date
            java.util.Date now = new java.util.Date();
            fb.setInsertDate(new Timestamp(now.getTime()));

            // Load a jOOQ-generated record from your POJO
            FeedbacksRecord fbRecord = dsl.newRecord(FEEDBACKS, fb);

            // Insert it
            fbRecord.store();

            return new ResponseEntity<String>(ErrorCause.FEEDBACK_OK.getJson(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(ErrorCause.ILLEGAL_ARGUMENT.getJson(), HttpStatus.BAD_REQUEST);
        }
    }

}