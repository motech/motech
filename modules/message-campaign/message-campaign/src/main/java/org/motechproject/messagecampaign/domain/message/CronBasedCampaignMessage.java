package org.motechproject.messagecampaign.domain.message;

import org.codehaus.jackson.annotate.JsonProperty;

public class CronBasedCampaignMessage extends CampaignMessage {

    @JsonProperty
    private String cron;

    public String cron() {
        return this.cron;
    }

    public void cron(String cron) {
        this.cron = cron;
    }
}
