package org.motechproject.server.messagecampaign.domain.message;

import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.userspecified.CampaignMessageRecord;
import org.motechproject.util.DateUtil;
import org.springframework.util.CollectionUtils;

import static org.apache.commons.lang.StringUtils.isEmpty;

public enum RepeatingMessageMode {

    REPEAT_INTERVAL {

        @Override
        public RepeatingCampaignMessage create(CampaignMessageRecord record) {
            return buildDefaultValues(new RepeatingCampaignMessage(record.repeatInterval()), record);
        }

        @Override
        public boolean isApplicable(RepeatingCampaignMessage message) {
            return true;
        }

    }, WEEK_DAYS_SCHEDULE {

        @Override
        public RepeatingCampaignMessage create(CampaignMessageRecord record) {
            return buildDefaultValues(new RepeatingCampaignMessage(record.weekDaysApplicable()), record);
        }
        @Override
        public boolean isApplicable(RepeatingCampaignMessage message) {

            int currentDayOfWeek = DateUtil.now().dayOfWeek().get();
            for (DayOfWeek dayOfWeek : message.weekDaysApplicable())
                if(dayOfWeek.getValue() == currentDayOfWeek) return true;
            return false;
        }
    }, CALENDAR_WEEK_SCHEDULE {

        @Override
        public RepeatingCampaignMessage create(CampaignMessageRecord record) {
            return buildDefaultValues(new RepeatingCampaignMessage(record.calendarStartOfWeek(), record.weekDaysApplicable()), record);
        }

        @Override
        public boolean isApplicable(RepeatingCampaignMessage message) {
            return true;
        }
    };

    public abstract RepeatingCampaignMessage create(CampaignMessageRecord record);
    public abstract boolean isApplicable(RepeatingCampaignMessage repeatingCampaignMessage);

    public RepeatingCampaignMessage buildDefaultValues(RepeatingCampaignMessage message, CampaignMessageRecord record) {
        message.name(record.name())
                .formats(record.formats())
                .languages(record.languages())
                .messageKey(record.messageKey());
        return message.mode(this);
    }

    public static RepeatingMessageMode findMode(CampaignMessageRecord record) {
        if(record.validate()) {
            if (!isEmpty(record.repeatInterval())) return REPEAT_INTERVAL;
            else if (!isEmpty(record.calendarStartOfWeek())) return CALENDAR_WEEK_SCHEDULE;
            else if (!CollectionUtils.isEmpty(record.weekDaysApplicable())) return WEEK_DAYS_SCHEDULE;
        }
        throw new IllegalArgumentException("expected repeatInterval or (calendarStartOfWeek, weekDaysApplicable) only");
    }
}
