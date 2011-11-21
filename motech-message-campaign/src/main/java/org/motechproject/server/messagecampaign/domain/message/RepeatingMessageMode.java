package org.motechproject.server.messagecampaign.domain.message;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static org.joda.time.Days.daysBetween;
import static org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage.WEEKLY_REPEAT_INTERVAL;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.valueobjects.factory.WallTimeFactory.create;

public enum RepeatingMessageMode {

    REPEAT_INTERVAL {

        public boolean isApplicable(RepeatingCampaignMessage message) {
            return true;
        }
        public Integer repeatIntervalForOffSet(RepeatingCampaignMessage message) {
            return create(message.repeatInterval()).inDays();
        }

        public Integer offset(RepeatingCampaignMessage message, Date startTime) {
            return (daysBetween(newDateTime(startTime).withTimeAtStartOfDay(),
                DateUtil.now().withTimeAtStartOfDay()).getDays() / repeatIntervalForOffSet(message)) + 1;
        }

    }, WEEK_DAYS_SCHEDULE {

        public boolean isApplicable(RepeatingCampaignMessage message) {
            return isWeekDayApplicable(message);
        }

        public Integer repeatIntervalForOffSet(RepeatingCampaignMessage message) {
            return WEEKLY_REPEAT_INTERVAL;
        }

        public Integer offset(RepeatingCampaignMessage message, Date startTime) {
            return (daysBetween(newDateTime(startTime).withTimeAtStartOfDay(),
                DateUtil.now().withTimeAtStartOfDay()).getDays() / repeatIntervalForOffSet(message)) + 1;
        }

    }, CALENDAR_WEEK_SCHEDULE {

        public boolean isApplicable(RepeatingCampaignMessage message) {
            return isWeekDayApplicable(message);
        }

        public Integer repeatIntervalForOffSet(RepeatingCampaignMessage message) {
            return WEEKLY_REPEAT_INTERVAL;
        }

        public Integer offset(RepeatingCampaignMessage message, Date cycleStartDate) {

            LocalDate cycleStartLocalDate = DateUtil.newDate(cycleStartDate);
            LocalDate currentDate = DateUtil.today();

            if(cycleStartDate.compareTo(currentDate.toDate()) > 0) throw new IllegalArgumentException("cycleStartDate cannot be in future");

            int daysDiff = new Period(cycleStartLocalDate, currentDate, PeriodType.days()).getDays();
            int currWeek = 1;
            if (daysDiff > 0) {
                int daysToFirstCalendarWeekEnd = daysToCalendarWeekEnd(cycleStartLocalDate, message.calendarStartDayOfWeek().getValue());
                int daysAfterFirstCalendarWeekEnd = daysDiff > daysToFirstCalendarWeekEnd ? daysDiff - daysToFirstCalendarWeekEnd : 0;
                int weeksAfterFirstSaturday = daysAfterFirstCalendarWeekEnd / 7 + (daysAfterFirstCalendarWeekEnd % 7 > 0 ? 1 : 0);
                return weeksAfterFirstSaturday + currWeek;
            }
            return currWeek;
        }

        private int daysToCalendarWeekEnd(LocalDate date, int calendarWeekStartDay) {
            int currentDayOfWeek = date.get(DateTimeFieldType.dayOfWeek());
            int calendarWeekEndDay = (calendarWeekStartDay + 6) % 7;
            int intervalBetweenWeekEndAndCurrentDay = calendarWeekEndDay - currentDayOfWeek;
            return intervalBetweenWeekEndAndCurrentDay > 0 ? intervalBetweenWeekEndAndCurrentDay :
                    intervalBetweenWeekEndAndCurrentDay + 7; 
        }
    };

    private static boolean isWeekDayApplicable(RepeatingCampaignMessage message) {
        int currentDayOfWeek = DateUtil.now().dayOfWeek().get();
        for (DayOfWeek dayOfWeek : message.weekDaysApplicable())
            if(dayOfWeek.getValue() == currentDayOfWeek) return true;
        return false;
    }

    public abstract boolean isApplicable(RepeatingCampaignMessage repeatingCampaignMessage);
    public abstract Integer offset(RepeatingCampaignMessage repeatingCampaignMessage, Date startTime);
    public abstract Integer repeatIntervalForOffSet(RepeatingCampaignMessage repeatingCampaignMessage);
}
