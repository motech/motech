package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MilestoneWindow implements Serializable {
    private WindowName name;
    private Period period;

    private List<Alert> alerts = new ArrayList<Alert>();

    public MilestoneWindow(WindowName name, WallTime start, WallTime end) {
        this.name = name;
        if (start.getUnit().equals(WallTimeUnit.Day))
            period = new Period(0, 0, 0, end.getValue() - start.getValue(), 0, 0, 0, 0);
        else if (start.getUnit().equals(WallTimeUnit.Week))
            period = new Period(0, 0, end.getValue() - start.getValue(), 0, 0, 0, 0, 0);
    }

    public WindowName getName() {
        return name;
    }

    public void addAlerts(Alert... alertsList) {
        alerts.addAll(Arrays.asList(alertsList));
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public int getWindowEndInDays() {
        return period.toStandardDays().getDays();
    }

}
