package org.motechproject.commcare.service.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.CommcareCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class CommcareCaseServiceIT {

    @Autowired
    private CommcareCaseService commcareCaseService;

    @Ignore
    @Test
    public void shouldReturnAllUsers() {
        assertNotNull(commcareCaseService);
        List<CaseInfo> caseInfoList = commcareCaseService.getAllCases();
        assertTrue(caseInfoList.size() > 0);
    }
}
