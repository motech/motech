package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This contains the alert timings of all the windows of a milestone
 */
public class MilestoneAlerts {
    private Map<String, List<DateTime>> alertTimings;

    /**
     * Constructor to create MilestoneAlerts
     */
    public MilestoneAlerts() {
        alertTimings = new HashMap<String, List<DateTime>>();
    }

    /**
     * Gives the alert timings for all the windows.
     *
     * Note : To get the alert timings of a particular window, use the corresponding getWindowAlertTimings method
     *
     * @return a map of window names to the list of their alert timings
     */
    public Map<String, List<DateTime>> getAlertTimings() {
        return alertTimings;
    }

    /**
     * Gives the alert timings of earliest window of the milestone
     *
     * @return List<DateTime> : represents the alert timings of earliest window
     */
    public List<DateTime> getEarliestWindowAlertTimings() {
        return alertTimings.get(WindowName.earliest.toString());
    }

    /**
     * Gives the alert timings of due window of the milestone
     *
     * @return List<DateTime> : represents the alert timings of due window
     */
    public List<DateTime> getDueWindowAlertTimings() {
        return alertTimings.get(WindowName.due.toString());
    }

    /**
     * Gives the alert timings of late window of the milestone
     *
     * @return List<DateTime> : represents the alert timings of late window
     */
    public List<DateTime> getLateWindowAlertTimings() {
        return alertTimings.get(WindowName.late.toString());
    }

    /**
     * Gives the alert timings of max window of the milestone
     *
     * @return List<DateTime> : represents the alert timings of max window
     */
    public List<DateTime> getMaxWindowAlertTimings() {
        return alertTimings.get(WindowName.max.toString());
    }
}
