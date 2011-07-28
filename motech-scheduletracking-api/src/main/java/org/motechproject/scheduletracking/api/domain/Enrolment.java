package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.valueobjects.WallTime;

import java.util.Date;

@TypeDiscriminator("doc.type === 'SCHEDULEENROLMENT'")
public class Enrolment  extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SCHEDULEENROLMENT";
    private String externalId;
    private Date enroledDate;
    private WallTime enroledIn;
    private String scheduleName;

    public Enrolment() {
    }

    public Enrolment(String externalId, Date enroledDate, WallTime enroledIn, String scheduleName) {
        this.externalId = externalId;
        this.enroledDate = enroledDate;
        this.enroledIn = enroledIn;
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

    public Date getEnroledDate() {
        return enroledDate;
    }

    public void setEnroledDate(Date enroledDate) {
        this.enroledDate = enroledDate;
    }

    public WallTime getEnroledIn() {
        return enroledIn;
    }

    public void setEnroledIn(WallTime enroledIn) {
        this.enroledIn = enroledIn;
    }
}
