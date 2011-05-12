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
package org.motechproject.server.tama.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ektorp.DocumentNotFoundException;
import org.motechproject.server.tama.service.DecisionTreeLookupService;
import org.motechproject.tama.api.dao.PatientDAO;
import org.motechproject.tama.api.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Spring MVC controller responsible for running rules for a patient ID using patient
 * to tree mapping service and redirect to the decision to VXML controller passing
 * in the patient id, tree name and language
 * 
 * @author yyonkov
 * 
 * {@code http://localhost:8081/motech-platform-server/module/tama/rules/tree?pId=10 }
 * 
 */
public class RulesController extends MultiActionController {
    public static final String PATIENT_ID_PARAM = "pId";
    public static final String LANGUAGE_PARAM = "ln";
    public static final String TREE_NAME_PARAM = "tNm";
    public static final String REDIRECT_LOGIN = "redirect:/tama/login";

    private Logger logger = LoggerFactory.getLogger((this.getClass()));
    
	@Autowired
	private DecisionTreeLookupService decisionTreeLookupService;
	@Autowired
	private PatientDAO patientDAO;

	public String tree(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String patientId = request.getParameter(PATIENT_ID_PARAM);
        if(patientId==null) {
        	logger.error("Invalid HTTP request - "+PATIENT_ID_PARAM+" is mandatory");
        	return REDIRECT_LOGIN;
        }

        String treeName;
        String language;
        try {
        	Patient patient = patientDAO.get(patientId);
        	language = patient.getPreferences().getLanguage().name();
        	logger.info("Running rules for patient with ID: "+patientId);
        	treeName = decisionTreeLookupService.findTreeNameByPatient(patient);
        } catch (DocumentNotFoundException e) {
        	logger.error("Patient with ID: "+patientId+" not found");
        	return REDIRECT_LOGIN;
        }
		// http://localhost:8081/motech-platform-server/module/tree/vxml/node	
		return String.format("redirect:/tree/vxml/node?"+PATIENT_ID_PARAM+"=%s&"+TREE_NAME_PARAM+"=%s&"+LANGUAGE_PARAM+"=%s", patientId, treeName, language);
	}
}
