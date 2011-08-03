package org.motechproject.scheduletracking.api.domain.enrolment;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'SCHEDULEENROLMENT'")
public class Enrolment extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SCHEDULEENROLMENT";
    private String externalId;
    private LocalDate enroledDate;
    private String scheduleName;
    private Map<WindowName, MilestoneFulfillment> fulfillments = new HashMap<WindowName, MilestoneFulfillment>();

    private Enrolment() {
    }

    public Enrolment(String externalId, LocalDate enroledDate, String scheduleName) {
        this.externalId = externalId;
        this.enroledDate = enroledDate;
        this.scheduleName = scheduleName;
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

    public LocalDate getEnroledDate() {
        return enroledDate;
    }

    public void setEnroledDate(LocalDate enroledDate) {
        this.enroledDate = enroledDate;
    }
}
