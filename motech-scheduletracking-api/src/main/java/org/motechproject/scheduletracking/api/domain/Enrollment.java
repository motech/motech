package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@TypeDiscriminator("doc.type === 'Enrollment'")
public class Enrollment extends MotechBaseDataObject {

    @JsonProperty
    private LocalDate enrollmentDate;

    private String externalId;

    private String scheduleName;
    private String currentMilestoneName;
    private LocalDate referenceDate;
    private Time preferredAlertTime;
    private List<MilestoneFulfillment> fulfillments = new LinkedList<MilestoneFulfillment>();

    // For ektorp
    private Enrollment() {
    }

    // TODO: enrollment can take scheduleName instead of schedule
    public Enrollment(String externalId, Schedule schedule, LocalDate referenceDate, LocalDate enrollmentDate, Time preferredAlertTime) {
        this.externalId = externalId;
        this.scheduleName = schedule.getName();
        this.enrollmentDate = enrollmentDate;
        this.referenceDate = referenceDate;
        this.preferredAlertTime = preferredAlertTime;
        if (schedule.getMilestones().size() > 0)
            this.currentMilestoneName = schedule.getMilestones().get(0).getName();
        else
            throw new InvalidScheduleDefinition("schedule must have at least one milestone.");
    }

    public Enrollment(String externalId, Schedule schedule, LocalDate referenceDate, LocalDate enrollmentDate, Time preferredAlertTime, String currentMilestoneName) {
        this.externalId = externalId;
        this.scheduleName = schedule.getName();
        this.enrollmentDate = enrollmentDate;
        this.referenceDate = referenceDate;
        this.preferredAlertTime = preferredAlertTime;
        this.currentMilestoneName = currentMilestoneName;
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

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
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

    public void fulfillCurrentMilestone(String nextMilestoneName, LocalDate dateFulfilled) {
        fulfillments.add(new MilestoneFulfillment(currentMilestoneName, dateFulfilled));
        currentMilestoneName = nextMilestoneName;
    }

    public LocalDate getLastFulfilledDate() {
        if (fulfillments.isEmpty())
            return null;
        return fulfillments.get(fulfillments.size() - 1).getDateFulfilled();
    }

    // ektorp methods follow
    private String getType() {
        return type;
    }

    private void setCurrentMilestoneName(String currentMilestoneName) {
        this.currentMilestoneName = currentMilestoneName;
    }

    private void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    private void setType(String type) {
        this.type = type;
    }

    private void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    private void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }
}