package org.motechproject.model;

import org.ektorp.support.TypeDiscriminator;

import java.util.Date;

/**
 *
 */
public class Appointment extends MotechAuditableDataObject {

     private static final long serialVersionUID = 1L;

    private String patientId;
    private Boolean patientArrived = false;
    private Date arrivalDate;
    private Date windowStartDate;
    @TypeDiscriminator
    private Date windowEndDate;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Boolean getPatientArrived() {
        return patientArrived;
    }

    public void setPatientArrived(Boolean patientArrived) {
        this.patientArrived = patientArrived;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getWindowStartDate() {
        return windowStartDate;
    }

    public void setWindowStartDate(Date windowStartDate) {
        this.windowStartDate = windowStartDate;
    }

    public Date getWindowEndDate() {
        return windowEndDate;
    }

    public void setWindowEndDate(Date windowEndDate) {
        this.windowEndDate = windowEndDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;

        if (arrivalDate != null ? !arrivalDate.equals(that.arrivalDate) : that.arrivalDate != null) return false;
        if (patientArrived != null ? !patientArrived.equals(that.patientArrived) : that.patientArrived != null)
            return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;
        if (windowEndDate != null ? !windowEndDate.equals(that.windowEndDate) : that.windowEndDate != null)
            return false;
        if (windowStartDate != null ? !windowStartDate.equals(that.windowStartDate) : that.windowStartDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = patientId != null ? patientId.hashCode() : 0;
        result = 31 * result + (patientArrived != null ? patientArrived.hashCode() : 0);
        result = 31 * result + (arrivalDate != null ? arrivalDate.hashCode() : 0);
        result = 31 * result + (windowStartDate != null ? windowStartDate.hashCode() : 0);
        result = 31 * result + (windowEndDate != null ? windowEndDate.hashCode() : 0);
        return result;
    }
}
