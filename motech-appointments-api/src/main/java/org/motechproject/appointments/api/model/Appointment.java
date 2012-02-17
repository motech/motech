package org.motechproject.appointments.api.model;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'Appointment'")
public class Appointment extends ExtensibleDataObject {

    private static final long serialVersionUID = 3L;

    private String externalId;
    private String title;
    private DateTime dueDate;
    private DateTime scheduledDate;
    private String visitId;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
    }

    public DateTime getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(DateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }
}
