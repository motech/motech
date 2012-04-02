package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.COMPLETED;
import static org.motechproject.util.DateUtil.setTimeZone;

@TypeDiscriminator("doc.type === 'Enrollment'")
public class Enrollment extends MotechBaseDataObject {
    @JsonProperty
    private String externalId;
    @JsonProperty
    private String scheduleName;
    @JsonProperty
    private String currentMilestoneName;
    @JsonProperty
    private DateTime startOfSchedule;
    @JsonProperty
    private DateTime enrolledOn;
    @JsonProperty
    private Time preferredAlertTime;
    @JsonProperty
    private EnrollmentStatus status;
    @JsonProperty
    private Map<String, String> metadata;

    private Schedule schedule;
    private List<MilestoneFulfillment> fulfillments = new LinkedList<MilestoneFulfillment>();

    // For ektorp
    private Enrollment() {
    }

    public Enrollment(String externalId, Schedule schedule, String currentMilestoneName, DateTime startOfSchedule, DateTime enrolledOn, Time preferredAlertTime, EnrollmentStatus enrollmentStatus, Map<String, String> metadata) {
        this.externalId = externalId;
        this.scheduleName = schedule.getName();
        this.schedule = schedule;
        this.currentMilestoneName = currentMilestoneName;
        this.startOfSchedule = startOfSchedule;
        this.enrolledOn = enrolledOn;
        this.preferredAlertTime = preferredAlertTime;
        this.status = enrollmentStatus;
        this.metadata = metadata;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getExternalId() {
        return externalId;
    }

    public DateTime getStartOfSchedule() {
        return setTimeZone(startOfSchedule);
    }

    public DateTime getEnrolledOn() {
        return setTimeZone(enrolledOn);
    }

    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

    public String getCurrentMilestoneName() {
        return currentMilestoneName;
    }

    public List<MilestoneFulfillment> getFulfillments() {
        return fulfillments;
    }

    @JsonIgnore
    public DateTime getLastFulfilledDate() {
        if (fulfillments.isEmpty())
            return null;
        return fulfillments.get(fulfillments.size() - 1).getFulfillmentDateTime();
    }

    public void fulfillCurrentMilestone(DateTime fulfillmentDateTime) {
        fulfillments.add(new MilestoneFulfillment(currentMilestoneName, fulfillmentDateTime));
    }

    @JsonIgnore
    public DateTime getReferenceForAlerts() {
        if (schedule.isBasedOnAbsoluteWindows()) {
            DateTime startOfSchedule = getStartOfSchedule();
            List<Milestone> milestones = schedule.getMilestones();
            for (Milestone milestone : milestones) {
                if (milestone.getName().equals(currentMilestoneName))
                    break;
                startOfSchedule = startOfSchedule.plus(milestone.getMaximumDuration());
            }
            return startOfSchedule;
        }
        if (currentMilestoneName.equals(schedule.getFirstMilestone().getName()))
            return getStartOfSchedule();
        return (fulfillments.isEmpty()) ? getEnrolledOn() : getLastFulfilledDate();
    }

    @JsonIgnore
    public boolean isActive() {
        return status.equals(ACTIVE);
    }

    @JsonIgnore
    public boolean isCompleted() {
        return status.equals(COMPLETED);
    }

    @JsonIgnore
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setCurrentMilestoneName(String currentMilestoneName) {
        this.currentMilestoneName = currentMilestoneName;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Enrollment copyFrom(Enrollment enrollment) {
        enrolledOn = enrollment.getEnrolledOn();
        currentMilestoneName = enrollment.getCurrentMilestoneName();
        startOfSchedule = enrollment.getStartOfSchedule();
        preferredAlertTime = enrollment.getPreferredAlertTime();
        return this;
    }

    public DateTime getStartOfWindowForCurrentMilestone(WindowName windowName) {
        DateTime currentMilestoneStartDate = getReferenceForAlerts();
        Milestone currentMilestone = schedule.getMilestone(currentMilestoneName);
        return currentMilestoneStartDate.plus(currentMilestone.getWindowStart(windowName));
    }
}