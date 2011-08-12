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
package org.motechproject.core;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.dao.RuleRepository;
import org.motechproject.model.Audit;
import org.motechproject.model.Rule;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by IntelliJ IDEA. User: rob Date: 2/28/11 Time: 1:47 PM To change
 * this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationPlatformCommon.xml"})
@PrepareForTest(DateUtil.class)
public class AuditablePersistenceIT {

    @Autowired
    private RuleRepository ruleRepository;

    private Rule rule;

    @org.junit.Rule
    public PowerMockRule junitRule = new PowerMockRule();

    @Before
    public void setup() throws Exception {
        mockStatic(DateUtil.class);
        rule = new Rule();
        rule.setContent("test");
    }

    @After
    public void tearDown() throws Exception {
        ruleRepository.remove(rule);
    }

    @Test
    public void testMotechPersistence() throws Exception {
        DateTime createDate = new DateTime(2011, 1, 1, 10, 20, 0, 0);
        DateTime updateDate = new DateTime(2011, 1, 1, 10, 25, 0, 0);

        when(DateUtil.now()).thenReturn(createDate);
        ruleRepository.add(rule);

        Audit audit = rule.getAudit();
        assertEquals(audit.getLastUpdated(), audit.getDateCreated());

        when(DateUtil.now()).thenReturn(updateDate);
        ruleRepository.update(rule);
        audit = rule.getAudit();
        assertEquals(5 * 60 * 1000, audit.getLastUpdated().getTime() - audit.getDateCreated().getTime());

        //fresh from db
        rule = ruleRepository.get(rule.getId());
        audit = rule.getAudit();
        assertEquals(5 * 60 * 1000, audit.getLastUpdated().getTime() - audit.getDateCreated().getTime());

        List<Rule> list = ruleRepository.getAll();
        assertTrue(list.size() > 0);
    }
}
