package org.motechproject.server.messagecampaign.scheduler;

public class JobIdFactory {

    public String getMessageJobIdFor(String campaignName, String externalId, String messageKey) {
        return String.format("MessageJob.%s.%s.%s", campaignName, externalId, messageKey);
    }
}
