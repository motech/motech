package org.motechproject.openmrs.model;

import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.model.Patient;

import java.util.Date;

public class OpenMRSPatient extends Patient{
    private Boolean insured;
    private String nhis;
    private Date nhisExpires;
    private OpenMRSPatient mother;
    private RegistrationMode registrationMode;
    private PatientType patientType;
    private Date estimateDateOfBirth;
    private Facility facility;
    private String address;

    public OpenMRSPatient() {
    }

    public Boolean getInsured() {
        return insured;
    }

    public String getNhis() {
        return nhis;
    }

    public Date getNhisExpires() {
        return nhisExpires;
    }

    public OpenMRSPatient getMother() {
        return mother;
    }

    public RegistrationMode getRegistrationMode() {
        return registrationMode;
    }

    public PatientType getPatientType() {
        return patientType;
    }

    public Date getEstimateDateOfBirth() {
        return estimateDateOfBirth;
    }

    public Facility getFacility() {
        return facility;
    }

    public String getAddress() {
        return address;
    }
}
