package org.motechproject.commcare.service.impl;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.util.CommCareAPIHttpClient;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommcareCaseServiceImplTest {

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
        when(
                commcareHttpClient.casesRequest(Matchers
                        .any(NameValuePair[].class))).thenReturn(
                casesResponse());

        List<CaseInfo> cases = caseService.getAllCases();

        assertEquals(asList("3ECE7ROKGQ7U1XX1DOL0PNRJW", "63ZB8WGEQY3TJ23PHB2EGD39J", "EP60PTXTZW6HD42KPSY9U018V",
                "EPKT93XZQ8COVAIQZ7DMQXO7S"), extract(cases, on(CaseInfo.class).getCaseId()));
    }

    @Test
    public void testCaseByCaseId() {
        String caseId = "testCase";

        NameValuePair[] queryParams = new NameValuePair[1];
        queryParams[0] = new NameValuePair("case_id", caseId);

        when(commcareHttpClient.casesRequest(queryParams)).thenReturn(
                singleCase());

        CaseInfo caseInstance = caseService.getCaseByCaseId(caseId);

        assertNotNull(caseInstance);
    }

    @Test
    public void testAllCasesByUserId() {
        String userId = "testId";

        NameValuePair[] queryParams = new NameValuePair[1];
        queryParams[0] = new NameValuePair("user_id", userId);

        when(commcareHttpClient.casesRequest(queryParams)).thenReturn(
                casesResponse());

        List<CaseInfo> cases = caseService.getAllCasesByUserId(userId);

        assertEquals(asList("3ECE7ROKGQ7U1XX1DOL0PNRJW", "63ZB8WGEQY3TJ23PHB2EGD39J", "EP60PTXTZW6HD42KPSY9U018V",
                "EPKT93XZQ8COVAIQZ7DMQXO7S"), extract(cases, on(CaseInfo.class).getCaseId()));
    }

    public void testCaseByCaseIdAndUserId() {
        String caseId = "testCase";
        String userId = "testId";

        NameValuePair[] queryParams = new NameValuePair[2];
        queryParams[0] = new NameValuePair("user_id", userId);
        queryParams[1] = new NameValuePair("case_id", caseId);

        when(commcareHttpClient.casesRequest(queryParams)).thenReturn(
                singleCase());

        CaseInfo caseInstance = caseService.getCaseByCaseIdAndUserId(caseId,
                userId);

        assertNotNull(caseInstance);
    }

    private String casesResponse() {
        return "[{\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"VPOIRH4A6XUUR3CH19XZH7SJC\", \"U69U2DPKVAIF4GOKZATU9EL87\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-04-09T19:48:07Z\", \"properties\": {\"bloodPressure\": \"69\", \"weight\": \"54\", \"height\": \"69\", \"case_type\": \"checkup\", \"location\": \"Ggg\", \"date_opened\": \"2012-04-09T11:03:36Z\", \"case_name\": \"1234\", \"external_id\": \"1234\", \"isPregnant\": \"pregnant\", \"owner_id\": null}, \"server_date_modified\": \"2012-04-09T19:48:07Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-04-09T15:48:00Z\", \"case_id\": \"3ECE7ROKGQ7U1XX1DOL0PNRJW\", \"closed\": false, \"indices\": {}}, {\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"XRM3LMWF4ERN4AHVQ89YTDQ29\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-04-12T18:49:15Z\", \"properties\": {\"case_type\": \"checkup\", \"date_opened\": \"2012-04-12T14:45:18Z\", \"external_id\": \"Eee\", \"owner_id\": null, \"case_name\": \"Eee\"}, \"server_date_modified\": \"2012-04-12T18:49:15Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-04-12T14:45:18Z\", \"case_id\": \"63ZB8WGEQY3TJ23PHB2EGD39J\", \"closed\": false, \"indices\": {}}, {\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"UH21I7LCZ756CV34ZE3TWISQZ\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-04-12T15:59:01Z\", \"properties\": {\"case_type\": \"checkup\", \"date_opened\": \"2012-04-12T11:58:49Z\", \"external_id\": \"Wer\", \"owner_id\": null, \"case_name\": \"Wer\"}, \"server_date_modified\": \"2012-04-12T15:59:01Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-04-12T11:58:49Z\", \"case_id\": \"EP60PTXTZW6HD42KPSY9U018V\", \"closed\": false, \"indices\": {}}, {\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"HZZ7B3KN3I6DKT8U55AG0D3QU\", \"F3QVVAQJEMNE1J3FU2FC4JJIA\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-04-10T14:31:44Z\", \"properties\": {\"bloodPressure\": \"56\", \"weight\": \"56\", \"height\": \"56\", \"case_type\": \"checkup\", \"location\": \"Gf\", \"date_opened\": \"2012-04-10T10:30:56Z\", \"case_name\": \"Aaaa\", \"external_id\": \"Aaaa\", \"isPregnant\": \"pregnant\", \"owner_id\": null}, \"server_date_modified\": \"2012-04-10T14:31:44Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-04-10T10:31:40Z\", \"case_id\": \"EPKT93XZQ8COVAIQZ7DMQXO7S\", \"closed\": false, \"indices\": {}}]";
    }

    private String singleCase() {
        return "[{\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"UPMW4L9RB4JWYFQYRDRWFKETV\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-04-12T18:59:04Z\", \"properties\": {\"case_type\": \"checkup\", \"date_opened\": \"2012-04-12T14:58:58Z\", \"external_id\": \"Gddd\", \"owner_id\": null, \"case_name\": \"Gddd\"}, \"server_date_modified\": \"2012-04-12T18:59:04Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-04-12T14:58:58Z\", \"case_id\": \"JQHFW1DBNQRQJ8VVKZ0M7RKJ4\", \"closed\": false, \"indices\": {\"parent\":{\"case_id\":\"ca43088e-471b-451c-b036-44edf63ad123\",\"case_type\":\"mother\"}}}]";
    }

}
