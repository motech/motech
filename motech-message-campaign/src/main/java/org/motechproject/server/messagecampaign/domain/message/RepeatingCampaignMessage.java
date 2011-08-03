package org.motechproject.server.messagecampaign.domain.message;

public class RepeatingCampaignMessage extends CampaignMessage {
    private String repeatInterval;

    public String repeatInterval() {
        return repeatInterval;
    }

    public void repeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
