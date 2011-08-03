package org.motechproject.server.messagecampaign.domain.campaign;

public enum CampaignType {

    ABSOLUTE, OFFSET, REPEATING, CRON;

    public Campaign instance() {
        switch (this) {
            case ABSOLUTE: return new AbsoluteCampaign();
            case OFFSET: return new OffsetCampaign();
            case REPEATING: return new RepeatingCampaign();
            case CRON: return new CronBasedCampaign();
            default: return null;

        }
    }

}
