package org.motechproject.server.messagecampaign.domain.message;

import java.util.Date;

public class AbsoluteCampaignMessage extends CampaignMessage {

    private Date date;

    public Date date() {
        return this.date;
    }

    public void date(Date date) {
        this.date = date;
    }
}
