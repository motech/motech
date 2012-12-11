package org.motechproject.server.messagecampaign.domain.message;

import org.joda.time.Period;

public class OffsetCampaignMessage extends CampaignMessage {

    private Period timeOffset;

    public OffsetCampaignMessage() {
    }

    public OffsetCampaignMessage(Period timeOffset) {
        this.timeOffset = timeOffset;
    }

    public Period timeOffset() {
        return timeOffset;
    }

    public void timeOffset(Period timeOffset) {
        this.timeOffset = timeOffset;
    }
}
