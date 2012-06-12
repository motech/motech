package org.motechproject.server.messagecampaign.domain.message;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.Constants;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import java.util.Date;

import static org.joda.time.Days.daysBetween;
import static org.joda.time.Hours.hoursBetween;
import static org.joda.time.Minutes.minutesBetween;
import static org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage.WEEKLY_REPEAT_INTERVAL;
import static org.motechproject.util.DateUtil.*;
import static org.motechproject.valueobjects.factory.WallTimeFactory.wallTime;

public enum RepeatingMessageMode {

    REPEAT_INTERVAL {
        public boolean isApplicable(RepeatingCampaignMessage message) {
            return true;
        }

        public Integer repeatIntervalForOffSet(RepeatingCampaignMessage message) {
            WallTime time = wallTime(message.repeatInterval());
            int interval;

            switch (time.getUnit()) {
                case Minute:
                    interval = time.inMinutes();
                    break;
                case Hour:
                    interval = time.inHours();
                    break;
                default:
                    interval = time.inDays();
            }

            return interval;
        }

        public Integer currentOffset(RepeatingCampaignMessage message, DateTime startTime, Integer startIntervalOffset) {
            WallTime time = wallTime(message.repeatInterval());
            int interval;

            switch (time.getUnit()) {
                case Minute:
                    interval = minutesBetween(startTime, DateUtil.now()).getMinutes();
                    break;
                case Hour:
                    interval = hoursBetween(startTime, DateUtil.now()).getHours();
                    break;
                default:
                    interval = daysBetween(newDate(startTime), today()).getDays();
            }

            return (interval / repeatIntervalForOffSet(message)) + 1;
        }

        public int durationInDaysToAdd(WallTime maxDuration, CampaignRequest campaignRequest, RepeatingCampaignMessage message) {
            return maxDuration.inDays() - 1;
        }

        @Override
        public DayOfWeek applicableWeekDayInNext24Hours(RepeatingCampaignMessage message) {
            DateTime applicableDateTime = applicableWeekDateInNext24Hours(message);
            return applicableDateTime != null ? DayOfWeek.getDayOfWeek(applicableDateTime.dayOfWeek()) : null;
        }

        // TODO: applicableWeekDayInNext24hours is wrong, so isEnded should be changed.
        @Override
        public boolean hasEnded(RepeatingCampaignMessage message, Date endTime) {
            return false;
        }

        private DateTime applicableWeekDateInNext24Hours(RepeatingCampaignMessage message) {
            return (message.isApplicable()) ? DateUtil.now() : null;
        }

    }, WEEK_DAYS_SCHEDULE {
        public boolean isApplicable(RepeatingCampaignMessage message) {
            return isTodayInApplicableWeekDays(message);
        }

        public Integer repeatIntervalForOffSet(RepeatingCampaignMessage message) {
            return WEEKLY_REPEAT_INTERVAL;
        }

        public Integer currentOffset(RepeatingCampaignMessage message, DateTime startTime, Integer startIntervalOffset) {
            int passedOffsetCycles = daysBetween(newDate(startTime), today()).getDays() / repeatIntervalForOffSet(message);
            return passedOffsetCycles + startIntervalOffset;
        }

        public int durationInDaysToAdd(WallTime maxDuration, CampaignRequest campaignRequest, RepeatingCampaignMessage message) {
            int elapsedOffsetInDays = (defaultOffsetIfNotSet(campaignRequest.startOffset()) - 1) * repeatIntervalForOffSet(message);
            return (maxDuration.inDays() - 1) - elapsedOffsetInDays;
        }

        @Override
        public DayOfWeek applicableWeekDayInNext24Hours(RepeatingCampaignMessage message) {
            DateTime applicableDateTime = applicableWeekDateInNext24Hours(message);
            return applicableDateTime != null ? DayOfWeek.getDayOfWeek(applicableDateTime.dayOfWeek()) : null;
        }

        @Override
        public boolean hasEnded(RepeatingCampaignMessage message, Date endTime) {
            DateTime nextApplicable = applicableWeekDateInNext24Hours(message);
            return nextApplicable!=null ? nextApplicableWeekDay(nextApplicable, message.weekDaysApplicable()).toDate().compareTo(endTime) >=0 :
                    now().compareTo(newDateTime(endTime)) >= 0;
        }

        private DateTime applicableWeekDateInNext24Hours(RepeatingCampaignMessage message) {
            return (message.isApplicable()) ? DateUtil.now() : null;
        }

    }, CALENDAR_WEEK_SCHEDULE {
        public boolean isApplicable(RepeatingCampaignMessage message) {
            return isTodayInApplicableWeekDays(message);
        }

        public Integer repeatIntervalForOffSet(RepeatingCampaignMessage message) {
            return WEEKLY_REPEAT_INTERVAL;
        }

        public Integer currentOffset(RepeatingCampaignMessage message, DateTime cycleStartDate, Integer startIntervalOffset) {

            LocalDate cycleStartLocalDate = DateUtil.newDate(cycleStartDate);
            LocalDate currentDate = DateUtil.today();

            if (cycleStartDate.isAfter(DateUtil.now()))
                throw new IllegalArgumentException("cycleStartDate cannot be in future");

            int daysDiff = new Period(cycleStartLocalDate, currentDate, PeriodType.days()).getDays();
            if (daysDiff > 0) {
                int daysToFirstCalendarWeekEnd = daysToCalendarWeekEnd(cycleStartLocalDate, message.calendarStartDayOfWeek().getValue());
                int daysAfterFirstCalendarWeekEnd = daysDiff > daysToFirstCalendarWeekEnd ? daysDiff - daysToFirstCalendarWeekEnd : 0;
                int weeksAfterFirstSaturday = daysAfterFirstCalendarWeekEnd / 7 + (daysAfterFirstCalendarWeekEnd % 7 > 0 ? 1 : 0);
                return weeksAfterFirstSaturday + startIntervalOffset;
            }
            return startIntervalOffset;
        }

        public int durationInDaysToAdd(WallTime maxDuration, CampaignRequest campaignRequest, RepeatingCampaignMessage message) {
            int actualMaxDurationDaysToAdd = maxDuration.inDays() - 1;
            int elapsedOffset = (defaultOffsetIfNotSet(campaignRequest.startOffset()) - 1) * repeatIntervalForOffSet(message);
            int daysToNormalizeForFirstWeek = daysToSubtractForRegisteredWeek(campaignRequest.referenceDate(), message.calendarStartDayOfWeek().getValue());
            return actualMaxDurationDaysToAdd - daysToNormalizeForFirstWeek - elapsedOffset;
        }

        @Override
        public DayOfWeek applicableWeekDayInNext24Hours(RepeatingCampaignMessage message) {
            DateTime applicableDateTime = applicableWeekDayTime(message);
            return applicableDateTime != null ? DayOfWeek.getDayOfWeek(applicableDateTime.dayOfWeek()) : null;
        }

        @Override
        public boolean hasEnded(RepeatingCampaignMessage message, Date endTime) {
            DateTime nextPossibleReminderDateTime = applicableWeekDayTime(message);
            if (nextPossibleReminderDateTime != null) {
                nextPossibleReminderDateTime = calculateNextPossibleIncludingFromDate(nextPossibleReminderDateTime.plusDays(1), message);
                return nextPossibleReminderDateTime.toDate().compareTo(endTime) >= 0;
            }
            return now().compareTo(newDateTime(endTime)) >= 0;
        }

        private DateTime applicableWeekDayTime(RepeatingCampaignMessage message) {
             DateTime now = DateUtil.now();
             DateTime nextPossibleReminderDateTime = calculateNextPossibleIncludingFromDate(now, message);
             DateTime oneDayLimit = now.plusDays(1);
             boolean isNextPossibleReminderTimeGreaterThanNow = nextPossibleReminderDateTime.toDate().compareTo(now.toDate()) >= 0;
             if (isNextPossibleReminderTimeGreaterThanNow && nextPossibleReminderDateTime.isBefore(oneDayLimit))
                 return nextPossibleReminderDateTime;
             return null;
         }

        private int daysToSubtractForRegisteredWeek(LocalDate startDate, int calendarStartDayOfWeek) {
            int daysToFirstWeekend = daysToCalendarWeekEnd(startDate, calendarStartDayOfWeek);
            // 0-6 based for daysToFirstWeekend
            return 6 - daysToFirstWeekend;
        }
    };

    private static int defaultOffsetIfNotSet(Integer offset) {
        return offset == null ? Constants.REPEATING_DEFAULT_START_OFFSET : offset;
    }

    private static boolean isTodayInApplicableWeekDays(RepeatingCampaignMessage message) {
        int currentDayOfWeek = DateUtil.now().dayOfWeek().get();
        for (DayOfWeek dayOfWeek : message.weekDaysApplicable())
            if (dayOfWeek.getValue() == currentDayOfWeek) return true;
        return false;
    }

    DateTime calculateNextPossibleIncludingFromDate(DateTime fromDate, RepeatingCampaignMessage message) {

        Time time = message.deliverTime();
        return DateUtil.nextApplicableWeekDayIncludingFromDate(fromDate, message.weekDaysApplicable()).withHourOfDay(time.getHour()).withMinuteOfHour(time.getMinute())
                .withSecondOfMinute(0).withMillisOfSecond(0);
    }

    public abstract boolean isApplicable(RepeatingCampaignMessage repeatingCampaignMessage);

    public abstract Integer currentOffset(RepeatingCampaignMessage repeatingCampaignMessage, DateTime startTime, Integer startIntervalOffset);

    public abstract Integer repeatIntervalForOffSet(RepeatingCampaignMessage repeatingCampaignMessage);

    public abstract int durationInDaysToAdd(WallTime maxDuration, CampaignRequest campaignRequest, RepeatingCampaignMessage message);

    public abstract DayOfWeek applicableWeekDayInNext24Hours(RepeatingCampaignMessage message);

    public abstract boolean hasEnded(RepeatingCampaignMessage message, Date endTime);
}
