package org.motechproject.messagecampaign.builder;

import org.motechproject.messagecampaign.EventKeys;

import java.util.HashMap;
import java.util.Map;

public class SchedulerPayloadBuilder {

    private Map<String, Object> params = new HashMap<String, Object>();

    public Map<String, Object> payload() {
        return params;
    }

    public SchedulerPayloadBuilder withJobId(String id) {
        params.put(EventKeys.SCHEDULE_JOB_ID_KEY, id);
        return this;
    }

    public SchedulerPayloadBuilder withCampaignName(String name) {
        params.put(EventKeys.CAMPAIGN_NAME_KEY, name);
        return this;
    }

    public SchedulerPayloadBuilder withExternalId(String id) {
        params.put(EventKeys.EXTERNAL_ID_KEY, id);
        return this;
    }

    public SchedulerPayloadBuilder withMessageKey(String messageKey) {
        params.put(EventKeys.MESSAGE_KEY, messageKey);
        return this;
    }
}
