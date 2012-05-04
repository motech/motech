package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Alert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static org.motechproject.util.DateUtil.*;


public class AlertWindow {
    private DateTime enrolledOn;
    private Alert alert;
    private DateTime alertWindowStart;
    private DateTime alertWindowEnd;
    private List<DateTime> schedulableAlertTimings;
    private Time preferredAlertTime;

    public AlertWindow(DateTime windowStart, DateTime windowEnd, DateTime enrolledOn, Time preferredAlertTime, Alert alert) {
        this.alertWindowStart = toPreferredTime(windowStart.plus(alert.getOffset()), preferredAlertTime);
        this.alertWindowEnd = windowEnd;
        this.preferredAlertTime = preferredAlertTime;
        this.enrolledOn = enrolledOn;
        this.alert = alert;

        List<DateTime> allAlertTimings = computeAllAlertTimings();
        schedulableAlertTimings = alertsFallingInAlertWindow(allAlertTimings);
    }

    public int numberOfAlertsToSchedule() {
        return schedulableAlertTimings.size();
    }

    public Date scheduledAlertStartDate() {
        if (schedulableAlertTimings.size() == 0) return null;
        return schedulableAlertTimings.get(0).toDate();
    }

    private List<DateTime> computeAllAlertTimings() {
        List<DateTime> alertTimings = new ArrayList<DateTime>();

        if(alert.getCount() > 0)  alertTimings.add(alertWindowStart);
        for (int alertIndex = 1; alertIndex < alert.getCount(); alertIndex++) {
            DateTime previousAlertTime = alertTimings.get(alertIndex - 1);
            alertTimings.add(previousAlertTime.plus(alert.getInterval()));
        }

        if (alert.isFloating() && alertStartDateTime().isAfter(alertWindowStart)) {
            return floatWindowAndAlertTimings(alertTimings);
        }

        return alertTimings;
    }

    private List<DateTime> floatWindowAndAlertTimings(List<DateTime> alertTimings) {
        List<DateTime> floatedAlertTimings = new ArrayList<DateTime>();

        DateTime preferredAlertStartDateTime = toPreferredTime(alertStartDateTime(), preferredAlertTime);
        Period periodToBeFloatedWith = new Period(alertWindowStart, preferredAlertStartDateTime);
        alertWindowStart = alertWindowStart.plus(periodToBeFloatedWith);

        for (DateTime alertTime : alertTimings)
            floatedAlertTimings.add(alertTime.plus(periodToBeFloatedWith));

        return floatedAlertTimings;
    }

    private List<DateTime> alertsFallingInAlertWindow(List<DateTime> alertTimings) {
        List<DateTime> alertsWithInEndDate = filter(lessThan(alertWindowEnd), alertTimings);

        DateTime alertToBeStartedOn = alertStartDateTime();
        List<DateTime> nonElapsedAlerts = filter(greaterThanOrEqualTo(alertToBeStartedOn), alertsWithInEndDate);

        if (hasAllAlertsElapsed(alertsWithInEndDate, nonElapsedAlerts)) {
            //todo [katta/v2] based on config, send one or none. Right now this sends one by default
            nonElapsedAlerts.add(0, toPreferredTime(alertToBeStartedOn, preferredAlertTime));
        }
        return nonElapsedAlerts;
    }

    private DateTime toPreferredTime(DateTime alertTime, Time preferredTime) {
        if (preferredTime == null) return alertTime;
        return newDateTime(alertTime.toLocalDate(), preferredTime.getHour(), preferredTime.getMinute(), 0);
    }

    private DateTime alertStartDateTime() {
        return now().isBefore(enrolledOn) ? enrolledOn : now();
    }

    private boolean hasAllAlertsElapsed(List<DateTime> alertsWithInEndDate, List<DateTime> nonElapsedAlerts) {
        return nonElapsedAlerts.size() == 0 && alertsWithInEndDate.size() > 0;
    }
}
