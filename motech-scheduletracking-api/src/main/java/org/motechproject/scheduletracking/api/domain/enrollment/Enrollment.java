package org.motechproject.scheduletracking.api.domain.enrollment;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.Schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TypeDiscriminator("doc.type === 'SCHEDULE_ENROLMENT'")
public class Enrollment extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SCHEDULE_ENROLMENT";
    private String externalId;
    private LocalDate enrolledDate;
    private String scheduleName;
    private Map<String, MilestoneFulfillment> fulfillments = new HashMap<String, MilestoneFulfillment>();
    private String nextMilestone;

    private Enrollment() {
    }

    public Enrollment(String externalId, LocalDate enrolledDate, String scheduleName, String firstMilestone) {
        this.externalId = externalId;
        this.enrolledDate = enrolledDate;
        this.scheduleName = scheduleName;
        this.nextMilestone = firstMilestone;
    }

    public List<Alert> getAlerts(Schedule schedule) {
        return schedule.alertsFor(getEnrolledDate(), nextMilestone);
    }

    public String fulfillMilestone(Schedule schedule) {
        MilestoneFulfillment fulfillment = new MilestoneFulfillment(LocalDate.now());
        fulfillments.put(nextMilestone, fulfillment);
        nextMilestone = schedule.nextMilestone(nextMilestone);
        return nextMilestone;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public LocalDate getEnrolledDate() {
        return enrolledDate;
    }

    public void setEnrolledDate(LocalDate enrolledDate) {
        this.enrolledDate = enrolledDate;
    }

    public String getNextMilestone() {
        return nextMilestone;
    }

    public Map<String, MilestoneFulfillment> getFulfillments() {
        return fulfillments;
    }
}
