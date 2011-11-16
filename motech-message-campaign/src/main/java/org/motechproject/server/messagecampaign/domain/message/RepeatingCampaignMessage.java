package org.motechproject.server.messagecampaign.domain.message;

import org.apache.commons.lang.StringUtils;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

public class RepeatingCampaignMessage extends CampaignMessage {

    private String repeatInterval;
    private List<DayOfWeek> weekDaysApplicable;
    public static final int DAILY_REPEAT_INTERVAL = 1;
    public static final int WEEKLY_REPEAT_INTERVAL = 7;

    public RepeatingCampaignMessage(String repeatInterval, List<String> weekDaysApplicable) {
        if (StringUtils.isEmpty(repeatInterval) && isEmpty(weekDaysApplicable) ||
                (isNotEmpty(repeatInterval) && !isEmpty(weekDaysApplicable)))
            throw new IllegalArgumentException("repeatInterval or weekdaysApplicable is expected");

        this.repeatInterval = repeatInterval;
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

    public List<DayOfWeek> getWeekDaysApplicable() {
        return weekDaysApplicable;
    }

    public boolean isApplicable() {
        if (isNotEmpty(repeatInterval)) return true;

        int currentDayOfWeek = DateUtil.now().dayOfWeek().get();
        for (DayOfWeek dayOfWeek : weekDaysApplicable)
            if(dayOfWeek.getValue() == currentDayOfWeek) return true;
        return false;
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
