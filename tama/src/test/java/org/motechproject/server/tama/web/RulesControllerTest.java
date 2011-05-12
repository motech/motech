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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ektorp.DocumentNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.server.tama.service.DecisionTreeLookupService;
import org.motechproject.tama.api.dao.PatientDAO;
import org.motechproject.tama.api.model.Patient;
import org.motechproject.tama.api.model.Preferences;


/**
 * Test for Rules Controller
 * @author yyonkov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RulesControllerTest {

    private static final String patientId = "10";
	private static final Patient patient = new Patient();
	private static final String treeName = "tree1";

	@InjectMocks
    RulesController rulesController = new RulesController();

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    

    @Mock
    private DecisionTreeLookupService decisionTreeLookupService;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Preferences pref = new Preferences();
        pref.setLanguage(Preferences.Language.fr);
        patient.setPreferences(pref);
    }
    
    @Test
    public void testTreeHappy() {
    	when(request.getParameter(RulesController.PATIENT_ID_PARAM)).thenReturn(patientId);
    	when(patientDAO.get(patientId)).thenReturn(patient);
    	when(decisionTreeLookupService.findTreeNameByPatient(patient)).thenReturn(treeName);
    	assertTrue(rulesController.tree(request, response).contains("redirect:/tree/vxml/node?"));
    	assertTrue(rulesController.tree(request, response).contains("ln=fr"));
    }
    
    @Test
    public void testTreeNoPid() {
    	when(request.getParameter(RulesController.PATIENT_ID_PARAM)).thenReturn(null);
    	when(patientDAO.get(patientId)).thenReturn(patient);
    	when(decisionTreeLookupService.findTreeNameByPatient(patient)).thenReturn(treeName);
    	assertEquals(RulesController.REDIRECT_LOGIN,rulesController.tree(request, response));
    }
    
    @Test
    public void testTreeNoPatient() {
    	when(request.getParameter(RulesController.PATIENT_ID_PARAM)).thenReturn(patientId);
    	when(patientDAO.get(patientId)).thenThrow(new DocumentNotFoundException(""));
    	when(decisionTreeLookupService.findTreeNameByPatient(patient)).thenReturn(treeName);
    	assertEquals(RulesController.REDIRECT_LOGIN,rulesController.tree(request, response));
    }    
}
