package org.motechproject.scheduletracking.api.domain.enrollment;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TypeDiscriminator("doc.type === 'SCHEDULEENROLMENT'")
public class Enrollment extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SCHEDULEENROLMENT";
    private String externalId;
    private LocalDate enrolledDate;
    private String scheduleName;
    private Map<WindowName, MilestoneFulfillment> fulfillments = new HashMap<WindowName, MilestoneFulfillment>();

    private Enrollment() {
    }

    public Enrollment(String externalId, LocalDate enrolledDate, String scheduleName) {
        this.externalId = externalId;
        this.enrolledDate = enrolledDate;
        this.scheduleName = scheduleName;
    }

    public List<Alert> getAlerts(Schedule schedule) {
        return schedule.alertsFor(getEnrolledDate());

//        ArrayList<Alert> alerts = new ArrayList<Alert>();
//
//        WindowName windowName = null;
//        Milestone milestone = null;
//        alerts.add(new Alert(windowName, schedule.milestone("One")));
//        return alerts;
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
}
