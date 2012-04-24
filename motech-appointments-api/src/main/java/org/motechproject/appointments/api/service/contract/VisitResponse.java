package org.motechproject.appointments.api.service.contract;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * \ingroup Appointments
 *
 * Visit details, returned by querying visits. see {@linkplain org.motechproject.appointments.api.service.AppointmentService#search(VisitsQuery)}
 */
public class VisitResponse {
    private String name;
    private String externalId;
    private String typeOfVisit;
    private DateTime visitDate;
    private boolean missed;
    private Map<String, Object> visitData = new HashMap<String, Object>();
    private DateTime originalAppointmentDueDate;
    private DateTime appointmentDueDate;
    private DateTime appointmentConfirmDate;

    public String getName() {
        return name;
    }

    public VisitResponse setName(String name) {
        this.name = name;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTypeOfVisit() {
        return typeOfVisit;
    }

    public VisitResponse setTypeOfVisit(String typeOfVisit) {
        this.typeOfVisit = typeOfVisit;
        return this;
    }

    public DateTime getVisitDate() {
        return visitDate;
    }

    public VisitResponse setVisitDate(DateTime visitDate) {
        this.visitDate = visitDate;
        return this;
    }

    public boolean isMissed() {
        return missed;
    }

    public VisitResponse setMissed(boolean missed) {
        this.missed = missed;
        return this;
    }

    public Map<String, Object> getVisitData() {
        return visitData;
    }

    public VisitResponse addVisitData(String key, Object value) {
        this.visitData.put(key, value);
        return this;
    }

    public VisitResponse setVisitData(Map<String, Object> visitData) {
        this.visitData = visitData;
        return this;
    }

    /**
     * Gets due date set while creating calendar. If due date was never changed then this will be same as {@link #appointmentDueDate}
     * @return
     */
    public DateTime getOriginalAppointmentDueDate() {
        return originalAppointmentDueDate;
    }

    public VisitResponse setOriginalAppointmentDueDate(DateTime originalAppointmentDueDate) {
        this.originalAppointmentDueDate = originalAppointmentDueDate;
        return this;
    }

    public DateTime getAppointmentDueDate() {
        return appointmentDueDate;
    }

    public VisitResponse setAppointmentDueDate(DateTime appointmentDueDate) {
        this.appointmentDueDate = appointmentDueDate;
        return this;
    }

    public DateTime getAppointmentConfirmDate() {
        return appointmentConfirmDate;
    }

    public VisitResponse setAppointmentConfirmDate(DateTime appointmentConfirmDate) {
        this.appointmentConfirmDate = appointmentConfirmDate;
        return this;
    }

    public boolean hasMetadata(String property, String value) {
        return visitData.containsKey(property) && visitData.get(property).equals(value);

    }
}