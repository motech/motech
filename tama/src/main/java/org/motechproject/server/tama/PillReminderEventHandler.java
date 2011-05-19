/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.tama;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.pillreminder.api.EventKeys;
import org.motechproject.pillreminder.api.PillReminderContext;
import org.motechproject.pillreminder.api.PillReminderService;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.service.ivr.CallInitiationException;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.api.dao.PatientDAO;
import org.motechproject.tama.api.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles Pill Reminder Events
 */
public class PillReminderEventHandler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    public static final String PATIENT_ID_PARAM = "pId";
    public static final String LANGUAGE_PARAM = "ln";
    public static final String TREE_NAME_PARAM = "tNm";

	private PillReminderService pillReminderService = PillReminderContext.getInstance().getPillReminderService();
	private IVRService ivrService = Context.getInstance().getIvrService();
	
	private String vxmlUrl;

	//TODO: should be a system wide configuration?
	private int timeOut = 20;

	@Autowired
	private PatientDAO patientDAO;

	@MotechListener(subjects = { EventKeys.PILLREMINDER_PUBLISH_EVENT_SUBJECT })
	public void handle(MotechEvent event) {
		String pillReminderId = EventKeys.getReminderID(event);
		if (pillReminderId == null) {
			log.error("Can not handle the Pill Reminder Event: " + event
					+ ". The event is invalid - missing the "
					+ EventKeys.PILLREMINDER_ID_KEY + " parameter");
			return;
		}

		PillReminder pillReminder = pillReminderService.getPillReminder(pillReminderId);
		if (pillReminder == null) {
			log.error("Can not handle the Pill Reminder Event: " + event
					+ ". The event is invalid - no pill reminder for id "
					+ pillReminderId);
			return;
		}

		String treeName = null;
		String language = null;
		String phoneNumber = null;
		try {
			Patient patient = patientDAO.get(pillReminder.getExternalId());
			phoneNumber = patient.getPhoneNumber();
			language = patient.getPreferences().getLanguage().name();
			List<String> medicineNames = pillReminderService.getMedicinesWithinWindow(pillReminder,	new Date());
			treeName = StringUtils.join(medicineNames, ",");
		} catch (Exception e) {
			log.error("Error at loading information for Pill Reminder ID:" + pillReminderId, e);
			return;
		}
		
		if (StringUtils.isEmpty(treeName)) {
			log.info("No medicine needs to be reminded for Pill Reminder ID:" + pillReminderId);
			return;
		}

		try {
			String vxmlUrlWithParams = String.format(vxmlUrl + "?" + PATIENT_ID_PARAM + "=%s&" + TREE_NAME_PARAM + "=%s&" + LANGUAGE_PARAM + "=%s", pillReminder.getExternalId(), treeName, language);
			CallRequest callRequest = new CallRequest(1l, phoneNumber, timeOut,	vxmlUrlWithParams);
			Map<String, Object> messageParameters = new HashMap<String, Object>();
			messageParameters.put(EventKeys.PILLREMINDER_ID_KEY, pillReminderId);

			MotechEvent incompleteEvent = new MotechEvent(EventKeys.PILLREMINDER_INCOMPLETE_SUBJECT, messageParameters);
			callRequest.setOnBusyEvent(incompleteEvent);
			callRequest.setOnFailureEvent(incompleteEvent);
			callRequest.setOnNoAnswerEvent(incompleteEvent);

			MotechEvent successEvent = new MotechEvent(EventKeys.PILLREMINDER_COMPLETE_SUBJECT, messageParameters);
			callRequest.setOnSuccessEvent(successEvent);

			ivrService.initiateCall(callRequest);
		} catch (CallInitiationException e) {
			log.error("Unable to initiate call to ExternalId:" + pillReminder.getExternalId(), e);
		}
	}
	
	public void setVxmlUrl(String vxmlUrl) {
		this.vxmlUrl = vxmlUrl;
	}
}
