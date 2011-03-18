package org.motechproject.model;

import org.ektorp.support.TypeDiscriminator;

/**
 *
 * TODO implement that class - depends on the domain model design task
 */
public class Patient extends MotechAuditableDataObject {

    private static final long serialVersionUID = 1L;

    @TypeDiscriminator
    private String clinicPatientId;

    
    public String getClinicPatientId() {
        return clinicPatientId;
    }

    public void setClinicPatientId(String clinicPatientId) {
        this.clinicPatientId = clinicPatientId;
    }
    
}
