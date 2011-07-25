package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.server.pillreminder.EventKeys;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class SchedulerPayloadBuilderTest {

    @Test
    public void shouldBuildASchedulerPayload() {
        HashMap payload = new SchedulerPayloadBuilder()
                .withJobId("jobId")
                .withPillRegimenId("pillRegimenId")
                .withDosageId("dosageId")
                .payload();
        assertEquals(payload.get(EventKeys.SCHEDULE_JOB_ID_KEY), "jobId");
        assertEquals(payload.get(EventKeys.PILLREMINDER_ID_KEY), "pillRegimenId");
        assertEquals(payload.get(EventKeys.DOSAGE_ID_KEY), "dosageId");
    }
}
