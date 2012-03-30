package org.motechproject.appointments.api.service.contract;

import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class VisitResponseTest {
    @Test
    public void shouldReturnTrueIfMetadataContainsKeyValuePair() {
        HashMap<String, Object> visitData = new HashMap<String, Object>();
        visitData.put("foo", "bar");
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setVisitData(visitData);

        assertEquals(true, visitResponse.hasMetadata("foo", "bar"));
        assertEquals(false, visitResponse.hasMetadata("foo", "barz"));
    }
}
