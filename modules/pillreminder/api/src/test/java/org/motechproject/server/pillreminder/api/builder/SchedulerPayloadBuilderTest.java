package org.motechproject.server.pillreminder.api.builder;

import org.junit.Test;
import org.motechproject.server.pillreminder.api.EventKeys;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SchedulerPayloadBuilderTest {

    @Test
    public void shouldBuildASchedulerPayload() {
        Map payload = new SchedulerPayloadBuilder()
                .withDosageId("dosageId")
                .payload();
        assertEquals(payload.get(EventKeys.DOSAGE_ID_KEY), "dosageId");
    }
}
