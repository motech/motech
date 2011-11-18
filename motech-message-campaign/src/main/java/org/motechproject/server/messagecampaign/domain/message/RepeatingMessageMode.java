package org.motechproject.server.messagecampaign.domain.message;

import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;

public enum RepeatingMessageMode {

    REPEAT_INTERVAL {

        @Override
        public boolean isApplicable(RepeatingCampaignMessage message) {
            return true;
        }

    }, WEEK_DAYS_SCHEDULE {

        @Override
        public boolean isApplicable(RepeatingCampaignMessage message) {

            int currentDayOfWeek = DateUtil.now().dayOfWeek().get();
            for (DayOfWeek dayOfWeek : message.weekDaysApplicable())
                if(dayOfWeek.getValue() == currentDayOfWeek) return true;
            return false;
        }
    }, CALENDAR_WEEK_SCHEDULE {

        @Override
        public boolean isApplicable(RepeatingCampaignMessage message) {
            return true;
        }
    };

    public abstract boolean isApplicable(RepeatingCampaignMessage repeatingCampaignMessage);
}
