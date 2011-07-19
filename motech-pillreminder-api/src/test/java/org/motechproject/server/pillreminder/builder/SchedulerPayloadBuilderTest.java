package org.motechproject.server.pillreminder.builder;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

public class SchedulerPayloadBuilderTest {

    @Test
    public void shouldBuildASchedulerPayload() {
        HashMap payload = new SchedulerPayloadBuilder()
                .withJobId("jobId")
                .withDosageId("dosageId")
                .payload();
        assertEquals(payload.get(EventKeys.SCHEDULE_JOB_ID_KEY), "jobId");
        assertEquals(payload.get(EventKeys.DOSAGE_ID_KEY), "dosageId");
    }
}
