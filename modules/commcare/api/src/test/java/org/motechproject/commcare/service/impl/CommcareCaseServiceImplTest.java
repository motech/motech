package org.motechproject.commcare.service.impl;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.request.json.CaseRequest;
import org.motechproject.commcare.util.CommCareAPIHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommcareCaseServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CommcareCaseServiceImpl caseService;

    @Mock
    private CommCareAPIHttpClient commcareHttpClient;

    @Before
    public void setUp() {
        initMocks(this);
        caseService = new CommcareCaseServiceImpl(commcareHttpClient);
    }

    @Test
    public void testAllCases() {
        when(commcareHttpClient.casesRequest(Matchers.any(CaseRequest.class))).thenReturn(casesResponse());

        List<CaseInfo> cases = caseService.getAllCases();

        assertEquals(asList("3ECE7ROKGQ7U1XX1DOL0PNRJW", "63ZB8WGEQY3TJ23PHB2EGD39J", "EP60PTXTZW6HD42KPSY9U018V",
                "EPKT93XZQ8COVAIQZ7DMQXO7S"), extract(cases, on(CaseInfo.class).getCaseId()));
    }

    @Test
    public void testCaseByCaseId() {
        String caseId = "testCase";

        when(commcareHttpClient.singleCaseRequest(caseId)).thenReturn(individualCase());

        CaseInfo caseInstance = caseService.getCaseByCaseId(caseId);

        assertNotNull(caseInstance);
    }

    @Test
    public void testAllCasesByUserId() {
        String userId = "testId";

        CaseRequest request = new CaseRequest();
        request.setUserId(userId);

        when(commcareHttpClient.casesRequest(request)).thenReturn(casesResponse());

        List<CaseInfo> cases = caseService.getAllCasesByUserId(userId);

        assertEquals(asList("3ECE7ROKGQ7U1XX1DOL0PNRJW", "63ZB8WGEQY3TJ23PHB2EGD39J", "EP60PTXTZW6HD42KPSY9U018V",
                "EPKT93XZQ8COVAIQZ7DMQXO7S"), extract(cases, on(CaseInfo.class).getCaseId()));
    }

    @Test
    public void testCaseByCaseIdAndUserId() {
        String caseId = "testCase";
        String userId = "testId";

        CaseRequest request = new CaseRequest();
        request.setUserId(userId);
        request.setCaseId(caseId);

        when(commcareHttpClient.casesRequest(request)).thenReturn(casesResponse());

        CaseInfo caseInstance = caseService.getCaseByCaseIdAndUserId(caseId, userId);

        assertNotNull(caseInstance);
    }

    private String casesResponse() {
        try {
            URL url = this.getClass().getClassLoader().getResource("json/service/cases.json");
            return FileUtils.readFileToString(new File(url.getFile()));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private String individualCase() {
        return "{\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"UPMW4L9RB4JWYFQYRDRWFKETV\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-04-12T18:59:04Z\", \"properties\": {\"case_type\": \"checkup\", \"date_opened\": \"2012-04-12T14:58:58Z\", \"external_id\": \"Gddd\", \"owner_id\": null, \"case_name\": \"Gddd\"}, \"server_date_modified\": \"2012-04-12T18:59:04Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-04-12T14:58:58Z\", \"case_id\": \"JQHFW1DBNQRQJ8VVKZ0M7RKJ4\", \"closed\": false, \"indices\": {\"parent\":{\"case_id\":\"ca43088e-471b-451c-b036-44edf63ad123\",\"case_type\":\"mother\"}}}";
    }

}
