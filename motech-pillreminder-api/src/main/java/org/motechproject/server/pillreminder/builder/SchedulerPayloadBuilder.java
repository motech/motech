package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.EventKeys;

import java.util.HashMap;

import static org.motechproject.server.pillreminder.EventKeys.SCHEDULE_JOB_ID_KEY;

public class SchedulerPayloadBuilder {

    private HashMap params = new HashMap();

    public SchedulerPayloadBuilder withJobId(String id) {
        params.put(SCHEDULE_JOB_ID_KEY, id);
        return this;
    }

    public SchedulerPayloadBuilder withDosageId(String id) {
        params.put(EventKeys.DOSAGE_ID_KEY, id);
        return this;
    }

    public HashMap payload() {
        return params;
    }
}
