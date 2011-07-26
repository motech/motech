package org.motechproject.server.messagecampaign.builder;

import org.motechproject.server.messagecampaign.EventKeys;

import java.util.HashMap;

public class SchedulerPayloadBuilder {
    private HashMap params = new HashMap();

    public SchedulerPayloadBuilder withJobId(String id) {
        params.put(EventKeys.SCHEDULE_JOB_ID_KEY, id);
        return this;
    }

    public SchedulerPayloadBuilder withMessageCampaignId(String id) {
        params.put(EventKeys.MESSAGECAMPAIGN_ID_KEY, id);
        return this;
    }

    public HashMap payload() {
        return params;
    }

}
