package org.motechproject.appointments.api.model;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.ExtensibleDataObject;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.type === 'Visit'")
public class Visit extends ExtensibleDataObject {
    private String externalId;
    private DateTime visitDate;
    private String title;
    private String appointmentId;
    private boolean missed;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public DateTime getVisitDate() {
        return DateUtil.setTimeZone(visitDate);
    }

    public void setVisitDate(DateTime visitDate) {
        this.visitDate = visitDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }
}
