package org.motechproject.server.messagecampaign.domain.message;

import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode.*;
import static org.springframework.util.CollectionUtils.isEmpty;

public class RepeatingCampaignMessage extends CampaignMessage {

    private RepeatingMessageMode repeatingMessageMode;

    private String repeatInterval;
    private String calendarStartOfWeek;
    private List<DayOfWeek> weekDaysApplicable;
    public static final int DAILY_REPEAT_INTERVAL = 1;
    public static final int WEEKLY_REPEAT_INTERVAL = 7;

    public RepeatingCampaignMessage(String repeatInterval) {
        this.repeatInterval = repeatInterval;
        this.repeatingMessageMode = REPEAT_INTERVAL;
    }

    public RepeatingCampaignMessage(List<String> weekDaysApplicable) {
        setWeekDaysApplicable(weekDaysApplicable);
        this.repeatingMessageMode = WEEK_DAYS_SCHEDULE;
    }

    public RepeatingCampaignMessage(String calendarStartOfWeek, List<String> weekDaysApplicable) {
        this.calendarStartOfWeek = calendarStartOfWeek;
        this.repeatingMessageMode = CALENDAR_WEEK_SCHEDULE;
        if(isEmpty(weekDaysApplicable)) this.weekDaysApplicable = asList(DayOfWeek.values());
        else setWeekDaysApplicable(weekDaysApplicable);
    }

    public int repeatIntervalForSchedule() {
        if (!isEmpty(weekDaysApplicable))
            return DAILY_REPEAT_INTERVAL;
        else
            return WallTimeFactory.create(repeatInterval).inDays();
    }

    public int repeatIntervalInDaysForOffset() {
       return this.repeatingMessageMode.repeatIntervalForOffSet(this);
    }

    public void repeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String repeatInterval() {
        return this.repeatInterval;
    }

    public List<DayOfWeek> weekDaysApplicable() {
        return weekDaysApplicable;
    }

    public void calendarStartOfWeek(String calendarStartOfWeek) {
        this.calendarStartOfWeek = calendarStartOfWeek;
    }

    public String calendarStartOfWeek() {
        return this.calendarStartOfWeek;
    }

    public DayOfWeek calendarStartDayOfWeek() {
        return DayOfWeek.valueOf(this.calendarStartOfWeek);
    }

    public boolean isApplicable() {
        return this.repeatingMessageMode.isApplicable(this);
    }

    public RepeatingCampaignMessage mode(RepeatingMessageMode repeatingMessageMode) {
        this.repeatingMessageMode = repeatingMessageMode;
        return this;
    }

    public RepeatingMessageMode mode() {
        return this.repeatingMessageMode;
    }

    private void setWeekDaysApplicable(List<String> weekDaysApplicable) {
        if (isEmpty(weekDaysApplicable)) return;

        List<DayOfWeek> applicableDays = new ArrayList<DayOfWeek>();
        for (String day : weekDaysApplicable) {
            applicableDays.add(DayOfWeek.valueOf(day));
        }
        this.weekDaysApplicable = applicableDays;
    }

    public Integer offset(Date startTime, Integer startIntervalOffset) {
        return repeatingMessageMode.offset(this, startTime, startIntervalOffset);
    }

    public int duration(WallTime duration, CampaignRequest campaignRequest) {
        return repeatingMessageMode.duration(duration, campaignRequest, this);
    }
}
