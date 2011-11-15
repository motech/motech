package org.motechproject.server.messagecampaign.domain.message;

import org.apache.commons.lang.StringUtils;
import org.motechproject.model.DayOfWeek;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

public class RepeatingCampaignMessage extends CampaignMessage {

    private String repeatInterval;
    private List<DayOfWeek> weekDaysApplicable;
    static final int DAILY_REPEAT_INTERVAL = 1;

    public RepeatingCampaignMessage(String repeatInterval, List<String> weekDaysApplicable) {
        if (StringUtils.isEmpty(repeatInterval) && isEmpty(weekDaysApplicable) ||
                (!StringUtils.isEmpty(repeatInterval) && !isEmpty(weekDaysApplicable)))
            throw new IllegalArgumentException("repeatInterval or weekdaysApplicable is expected");

        this.repeatInterval = repeatInterval;
        setWeekDaysApplicable(weekDaysApplicable);
    }

    public int repeatIntervalInDays() {
        if (!isEmpty(weekDaysApplicable))
            return DAILY_REPEAT_INTERVAL;
        else
            return WallTimeFactory.create(repeatInterval).inDays();
    }

    public void repeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public List<DayOfWeek> getWeekDaysApplicable() {
        return weekDaysApplicable;
    }

    private void setWeekDaysApplicable(List<String> weekDaysApplicable) {
        if (isEmpty(weekDaysApplicable)) return;

        List<DayOfWeek> applicableDays = new ArrayList<DayOfWeek>();
        for (String day : weekDaysApplicable) {
            applicableDays.add(DayOfWeek.valueOf(day));
        }
        this.weekDaysApplicable = applicableDays;
    }
}
