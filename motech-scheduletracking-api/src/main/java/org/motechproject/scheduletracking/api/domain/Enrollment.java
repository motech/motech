package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;

import java.util.LinkedList;
import java.util.List;

import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.COMPLETED;
import static org.motechproject.util.DateUtil.setTimeZone;

@TypeDiscriminator("doc.type == 'Enrollment'")
public class Enrollment extends MotechBaseDataObject {
    @JsonProperty
    private DateTime enrollmentDateTime;
    @JsonProperty
    private String scheduleName;
    @JsonProperty
    private String externalId;
    private String currentMilestoneName;
    @JsonProperty
    private DateTime referenceDateTime;
    @JsonProperty
    private Time preferredAlertTime;

    private EnrollmentStatus status;
    private List<MilestoneFulfillment> fulfillments = new LinkedList<MilestoneFulfillment>();

    // For ektorp
    private Enrollment() {
    }

    public Enrollment(String externalId, String scheduleName, String currentMilestoneName, DateTime referenceDateTime, DateTime enrollmentDateTime, Time preferredAlertTime, EnrollmentStatus enrollmentStatus) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.currentMilestoneName = currentMilestoneName;
        this.enrollmentDateTime = enrollmentDateTime;
        this.referenceDateTime = referenceDateTime;
        this.preferredAlertTime = preferredAlertTime;
        this.status = enrollmentStatus;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getExternalId() {
        return externalId;
    }

    public DateTime getReferenceDateTime() {
        return setTimeZone(referenceDateTime);
    }

    public DateTime getEnrollmentDateTime() {
        return setTimeZone(enrollmentDateTime);
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

    @JsonIgnore
    public boolean isActive() {
        return status.equals(ACTIVE);
    }

    @JsonIgnore
    public boolean isCompleted() {
        return status.equals(COMPLETED);
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
        enrollmentDateTime = enrollment.getEnrollmentDateTime();
        currentMilestoneName = enrollment.getCurrentMilestoneName();
        referenceDateTime = enrollment.getReferenceDateTime();
        preferredAlertTime = enrollment.getPreferredAlertTime();
        return this;
    }

    // ektorp methods follow
    private String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public void fulfillCurrentMilestone(DateTime fulfillmentDateTime) {
        fulfillments.add(new MilestoneFulfillment(currentMilestoneName, fulfillmentDateTime));
    }
}