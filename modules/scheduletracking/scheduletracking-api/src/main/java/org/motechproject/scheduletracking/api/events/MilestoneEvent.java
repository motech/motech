package org.motechproject.scheduletracking.api.events;

import org.joda.time.DateTime;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.MilestoneWindow;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;

import java.util.HashMap;

/**
 * This is the event which will be raised as per the alert configuration
 */
public class MilestoneEvent {
    private String windowName;
    private MilestoneAlert milestoneAlert;
    private String scheduleName;
    private String externalId;
    private DateTime referenceDateTime;

    /**
     * Creates a MilestoneEvent
     * @param externalId
     * @param scheduleName
     * @param milestoneAlert
     * @param windowName
     * @param referenceDateTime
     */
    public MilestoneEvent(String externalId, String scheduleName, MilestoneAlert milestoneAlert, String windowName, DateTime referenceDateTime) {
        this.scheduleName = scheduleName;
        this.milestoneAlert = milestoneAlert;
        this.windowName = windowName;
        this.externalId = externalId;
        this.referenceDateTime = referenceDateTime;
    }

    /**
     * Creates a MilestoneEvent from an Enrollment by passing in an MotechEvent
     * @param motechEvent
     */
    public MilestoneEvent(MotechEvent motechEvent) {
        this.scheduleName = (String) motechEvent.getParameters().get(EventDataKeys.SCHEDULE_NAME);
        this.milestoneAlert = (MilestoneAlert) motechEvent.getParameters().get(EventDataKeys.MILESTONE_NAME);
        this.windowName = (String) motechEvent.getParameters().get(EventDataKeys.WINDOW_NAME);
        this.externalId = (String) motechEvent.getParameters().get(EventDataKeys.EXTERNAL_ID);
        this.referenceDateTime = (DateTime) motechEvent.getParameters().get(EventDataKeys.REFERENCE_DATE);
    }

    /**
     * Creates a MilestoneEvent from an Enrollment
     * @param enrollment
     * @param milestoneAlert
     * @param milestoneWindow
     */
    public MilestoneEvent(Enrollment enrollment, MilestoneAlert milestoneAlert, MilestoneWindow milestoneWindow) {
        this.externalId = enrollment.getExternalId();
        this.scheduleName = enrollment.getScheduleName();
        this.milestoneAlert = milestoneAlert;
        this.windowName = milestoneWindow.getName().toString();
        this.referenceDateTime = enrollment.getStartOfSchedule();
    }

    /**
     * Creates an MotechEvent from a MilestoneEvent
     * @return MotechEvent
     */
    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKeys.WINDOW_NAME, windowName);
        parameters.put(EventDataKeys.MILESTONE_NAME, milestoneAlert);
        parameters.put(EventDataKeys.SCHEDULE_NAME, scheduleName);
        parameters.put(EventDataKeys.EXTERNAL_ID, externalId);
        parameters.put(EventDataKeys.REFERENCE_DATE, referenceDateTime);
        return new MotechEvent(EventSubjects.MILESTONE_ALERT, parameters);
    }

    /**
     * Returns the Window name of the MilestoneEvent
     * @return String
     */
    public String getWindowName() {
        return windowName;
    }

    /**
     * Returns the MilestoneAlert of the MilestoneEvent
     * @return MilestoneAlert
     */
    public MilestoneAlert getMilestoneAlert() {
        return milestoneAlert;
    }

    /**
     * Returns the Schedule Name of the MilestoneEvent
     * @return String
     */
    public String getScheduleName() {
        return scheduleName;
    }

    /**
     * Returns the External Id of the MilestoneEvent
     * @return String
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Returns the ReferenceDateTime of the MilestoneEvent
     * @return DateTime
     */
    public DateTime getReferenceDateTime() {
        return referenceDateTime;
    }
}
