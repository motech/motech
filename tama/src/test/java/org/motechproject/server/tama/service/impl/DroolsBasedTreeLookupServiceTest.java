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
package org.motechproject.server.tama.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.net.URLDecoder;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.dao.RuleRepository;
import org.motechproject.server.ruleengine.KnowledgeBaseManager;
import org.motechproject.tama.model.Patient;
import org.motechproject.tama.model.Regimen;



public class DroolsBasedTreeLookupServiceTest {
	
	KnowledgeBaseManager kbm;
	String ruleFolder = "/rules";
	String ruleFile = "SymptomTreeLookupRules.drl";
	
	DroolsBasedDecisionTreeLookupService service = new DroolsBasedDecisionTreeLookupService();
	
	@Before
	public void setup() throws Exception {
		kbm = new KnowledgeBaseManager();
		RuleRepository repo = mock(RuleRepository.class);
		kbm.setRuleRepository(repo);
		File file = new File(URLDecoder.decode(getClass().getResource(ruleFolder + "/" + ruleFile).getFile(), "UTF-8"));
		kbm.addOrUpdateRule(file);
		service.setKnowledgeBaseManager(kbm);
		service.setRuleId(ruleFile);
	}
	
    @Test
    public void findTreeNameByPatientTest() throws Exception {

        Patient p1 = new Patient();
        //p1.setId("abc");
        p1.setDateOfBirth(new DateTime().minusYears(51).toDate());
        p1.setRegistrationDate(new DateTime().minusMonths(7).toDate());
        p1.setRegimen(Regimen.REGIMEN_1);
        
        String treeName = service.findTreeNameByPatient(p1);
        assertEquals("age-over-50-regimen1-registered-over-180", treeName);
        
        p1.setDateOfBirth(new DateTime().minusYears(49).toDate());
        treeName = service.findTreeNameByPatient(p1);
        assertEquals("age-under-50-or-non-regimen1-or-registered-less-than-180", treeName);
        
        p1.setDateOfBirth(new DateTime().minusYears(51).toDate());
        p1.setRegistrationDate(new DateTime().minusMonths(5).toDate());
        treeName = service.findTreeNameByPatient(p1);
        assertEquals("age-under-50-or-non-regimen1-or-registered-less-than-180", treeName);
        
        p1.setRegistrationDate(new DateTime().minusMonths(7).toDate());
        p1.setRegimen(Regimen.REGIMEN_2);
        treeName = service.findTreeNameByPatient(p1);
        assertEquals("age-under-50-or-non-regimen1-or-registered-less-than-180", treeName);
    }
    
}

