package org.motechproject.scheduletracking.api.events;

import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.MilestoneWindow;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;

import java.util.HashMap;

public class MilestoneEvent {
    private String windowName;
    private MilestoneAlert milestoneAlert;
    private String scheduleName;
    private String externalId;
    private LocalDate referenceDate;

    public MilestoneEvent(String externalId, String scheduleName, MilestoneAlert milestoneAlert, String windowName, LocalDate referenceDate) {
        this.scheduleName = scheduleName;
        this.milestoneAlert = milestoneAlert;
        this.windowName = windowName;
        this.externalId = externalId;
        this.referenceDate = referenceDate;
    }

    public MilestoneEvent(MotechEvent motechEvent) {
        this.scheduleName = (String) motechEvent.getParameters().get(EventDataKeys.SCHEDULE_NAME);
        this.milestoneAlert = (MilestoneAlert) motechEvent.getParameters().get(EventDataKeys.MILESTONE_NAME);
        this.windowName = (String) motechEvent.getParameters().get(EventDataKeys.WINDOW_NAME);
        this.externalId = (String) motechEvent.getParameters().get(EventDataKeys.EXTERNAL_ID);
        this.referenceDate = (LocalDate) motechEvent.getParameters().get(EventDataKeys.REFERENCE_DATE);
    }

    public MilestoneEvent(Enrollment enrollment, MilestoneAlert milestoneAlert, MilestoneWindow milestoneWindow) {
        this.externalId = enrollment.getExternalId();
        this.scheduleName = enrollment.getScheduleName();
        this.milestoneAlert = milestoneAlert;
        this.windowName = milestoneWindow.getName().toString();
        this.referenceDate = enrollment.getReferenceDate();
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKeys.WINDOW_NAME, windowName);
        parameters.put(EventDataKeys.MILESTONE_NAME, milestoneAlert);
        parameters.put(EventDataKeys.SCHEDULE_NAME, scheduleName);
        parameters.put(EventDataKeys.EXTERNAL_ID, externalId);
        parameters.put(EventDataKeys.REFERENCE_DATE, referenceDate);
        return new MotechEvent(EventSubjects.MILESTONE_ALERT, parameters);
    }

    public String getWindowName() {
        return windowName;
    }

    public MilestoneAlert getMilestoneAlert() {
        return milestoneAlert;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getExternalId() {
        return externalId;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }
}
