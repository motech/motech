package org.motechproject.messagecampaign.domain.campaign;

public enum CampaignType {

    ABSOLUTE {
        @Override
        public Campaign instance() {
            return new AbsoluteCampaign();
        }
    }, OFFSET {
        @Override
        public Campaign instance() {
            return new OffsetCampaign();
        }
    }, REPEAT_INTERVAL {
        @Override
        public Campaign instance() {
            return new RepeatIntervalCampaign();
        }
    }, DAY_OF_WEEK {
        @Override
        public Campaign instance() {
            return new DayOfWeekCampaign();
        }
    }, CRON {
        @Override
        public Campaign instance() {
            return new CronBasedCampaign();
        }
    };

    public abstract Campaign instance();

}
