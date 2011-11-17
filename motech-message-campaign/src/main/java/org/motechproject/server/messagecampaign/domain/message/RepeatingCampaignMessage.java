package org.motechproject.server.messagecampaign.domain.message;

import org.apache.commons.lang.StringUtils;
import org.motechproject.model.DayOfWeek;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.ArrayList;
import java.util.List;

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
    }

    public RepeatingCampaignMessage(List<String> weekDaysApplicable) {
        setWeekDaysApplicable(weekDaysApplicable);
    }

    public RepeatingCampaignMessage(String calendarStartOfWeek, List<String> weekDaysApplicable) {
        this.calendarStartOfWeek = calendarStartOfWeek;
        setWeekDaysApplicable(weekDaysApplicable);
    }

    public int repeatIntervalForSchedule() {
        if (!isEmpty(weekDaysApplicable))
            return DAILY_REPEAT_INTERVAL;
        else
            return WallTimeFactory.create(repeatInterval).inDays();
    }

    public int repeatIntervalInDaysForOffset() {
        if (!isEmpty(weekDaysApplicable))
            return WEEKLY_REPEAT_INTERVAL;
        else
            return WallTimeFactory.create(repeatInterval).inDays();
    }

    public void repeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
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

    public boolean isApplicable() {
        return this.repeatingMessageMode.isApplicable(this);
    }

    public RepeatingCampaignMessage mode(RepeatingMessageMode repeatingMessageMode) {
        this.repeatingMessageMode = repeatingMessageMode;
        return this;
    }

    private void setWeekDaysApplicable(List<String> weekDaysApplicable) {
        if (isEmpty(weekDaysApplicable)) return;

        List<DayOfWeek> applicableDays = new ArrayList<DayOfWeek>();
        for (String day : weekDaysApplicable) {
            applicableDays.add(DayOfWeek.valueOf(day));
        }
        this.weekDaysApplicable = applicableDays;
    }

    private boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }
}
