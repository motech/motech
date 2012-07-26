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

    public Enrollment() {
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public Enrollment setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public Enrollment setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public Enrollment setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public DateTime getStartOfSchedule() {
        return setTimeZone(startOfSchedule);
    }

    public Enrollment setStartOfSchedule(DateTime startOfSchedule) {
        this.startOfSchedule = startOfSchedule;
        return this;
    }

    public DateTime getEnrolledOn() {
        return setTimeZone(enrolledOn);
    }

    public Enrollment setEnrolledOn(DateTime enrolledOn) {
        this.enrolledOn = enrolledOn;
        return this;
    }

    public Time getPreferredAlertTime() {
        return preferredAlertTime;
    }

    public Enrollment setPreferredAlertTime(Time preferredAlertTime) {
        this.preferredAlertTime = preferredAlertTime;
        return this;
    }

    public List<MilestoneFulfillment> getFulfillments() {
        return fulfillments;
    }

    public Enrollment setFulfillments(List<MilestoneFulfillment> fulfillments) {
        this.fulfillments = fulfillments;
        return this;
    }

    @JsonIgnore
    public DateTime getLastFulfilledDate() {
        if (fulfillments.isEmpty()) {
            return null;
        }
        return fulfillments.get(fulfillments.size() - 1).getFulfillmentDateTime();
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

    public Enrollment setSchedule(Schedule schedule) {
        this.schedule = schedule;
        this.scheduleName = schedule.getName();
        return this;
    }

    public String getCurrentMilestoneName() {
        return currentMilestoneName;
    }

    public Enrollment setCurrentMilestoneName(String currentMilestoneName) {
        this.currentMilestoneName = currentMilestoneName;
        return this;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public Enrollment setStatus(EnrollmentStatus status) {
        this.status = status;
        return this;
    }

    public void fulfillCurrentMilestone(DateTime fulfillmentDateTime) {
        fulfillments.add(new MilestoneFulfillment(currentMilestoneName, fulfillmentDateTime));
    }

    @JsonIgnore
    public DateTime getStartOfWindowForCurrentMilestone(WindowName windowName) {
        DateTime currentMilestoneStartDate = getCurrentMilestoneStartDate();
        Milestone currentMilestone = schedule.getMilestone(currentMilestoneName);
        return currentMilestoneStartDate.plus(currentMilestone.getWindowStart(windowName));
    }

    @JsonIgnore
    public DateTime getCurrentMilestoneStartDate() {
        if (schedule.isBasedOnAbsoluteWindows()) {
            DateTime startOfMilestone = getStartOfSchedule();
            List<Milestone> milestones = schedule.getMilestones();
            for (Milestone milestone : milestones) {
                if (milestone.getName().equals(currentMilestoneName)) {
                    break;
                }
                startOfMilestone = startOfMilestone.plus(milestone.getMaximumDuration());
            }
            return startOfMilestone;
        }
        if (currentMilestoneName.equals(schedule.getFirstMilestone().getName())) {
            return getStartOfSchedule();
        }
        return (fulfillments.isEmpty()) ? getEnrolledOn() : getLastFulfilledDate();
    }

    public Enrollment copyFrom(Enrollment enrollment) {
        enrolledOn = enrollment.getEnrolledOn();
        currentMilestoneName = enrollment.getCurrentMilestoneName();
        startOfSchedule = enrollment.getStartOfSchedule();
        preferredAlertTime = enrollment.getPreferredAlertTime();
        return this;
    }
}
