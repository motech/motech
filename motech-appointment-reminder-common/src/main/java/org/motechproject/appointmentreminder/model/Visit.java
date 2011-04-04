package org.motechproject.appointmentreminder.model;

import java.util.Date;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

public class Visit extends MotechBaseDataObject {

	private static final long serialVersionUID = -3934731398961846431L;
	@TypeDiscriminator
	private String patientId;
    private Date visitDate;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visit visit = (Visit) o;

        if (patientId != null ? !patientId.equals(visit.patientId) : visit.patientId != null) return false;
        if (visitDate != null ? !visitDate.equals(visit.visitDate) : visit.visitDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = patientId != null ? patientId.hashCode() : 0;
        result = 31 * result + (visitDate != null ? visitDate.hashCode() : 0);
        return result;
    }
}
