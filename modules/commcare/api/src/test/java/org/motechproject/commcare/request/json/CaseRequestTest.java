package org.motechproject.commcare.request.json;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class CaseRequestTest {

    @Test
    public void shouldConvertToQueryString() {
        CaseRequest caseRequest = getCaseRequest();
        assertThat(caseRequest.toQueryString(), IsEqual.equalTo("user_id=U100&case_id=C100&type=sometype&limit=100&offset=200"));
    }

    @Test
    public void shouldSkipEmptyQueryParams() {
        CaseRequest caseRequest = getCaseRequest();
        caseRequest.setCaseId(null);
        assertThat(caseRequest.toQueryString(), IsEqual.equalTo("user_id=U100&type=sometype&limit=100&offset=200"));

        caseRequest.setUserId(null);
        assertThat(caseRequest.toQueryString(), IsEqual.equalTo("type=sometype&limit=100&offset=200"));

        caseRequest.setType(null);
        assertThat(caseRequest.toQueryString(), IsEqual.equalTo("limit=100&offset=200"));
    }

    private CaseRequest getCaseRequest() {
        CaseRequest caseRequest = new CaseRequest();
        caseRequest.setUserId("U100");
        caseRequest.setCaseId("C100");
        caseRequest.setType("sometype");
        caseRequest.setLimit(100);
        caseRequest.setOffset(200);
        return caseRequest;
    }
}
