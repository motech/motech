package org.motechproject.openmrs.model;

import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.model.Patient;

import java.util.Date;

public class OpenMRSPatient extends Patient{
    private Boolean insured;
    private String nhis;
    private Date nhisExpires;
    private OpenMRSPatient mother;
    private PatientType patientType;
    private Boolean estimateDateOfBirth;
    private Facility facility;

    public OpenMRSPatient() {
    }

    public OpenMRSPatient(Builder builder) {
        super(builder.id, builder.middleName, builder.lastName, builder.prefferedName, builder.dateOfBirth, builder.gender, builder.address);
        this.insured = builder.insured;
        this.nhis = builder.nhis;
        this.nhisExpires = builder.nhisExpires;
        this.mother = builder.mother;
        this.patientType = builder.patientType;
        this.estimateDateOfBirth = builder.estimateDateOfBirth;
        this.facility = builder.facility;
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

    public PatientType getPatientType() {
        return patientType;
    }

    public Boolean getEstimateDateOfBirth() {
        return estimateDateOfBirth;
    }

    public Facility getFacility() {
        return facility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenMRSPatient)) return false;
        if (!super.equals(o)) return false;

        OpenMRSPatient that = (OpenMRSPatient) o;

        if (estimateDateOfBirth != null ? !estimateDateOfBirth.equals(that.estimateDateOfBirth) : that.estimateDateOfBirth != null)
            return false;
        if (facility != null ? !facility.equals(that.facility) : that.facility != null) return false;
        if (insured != null ? !insured.equals(that.insured) : that.insured != null) return false;
        if (mother != null ? !mother.equals(that.mother) : that.mother != null) return false;
        if (nhis != null ? !nhis.equals(that.nhis) : that.nhis != null) return false;
        if (nhisExpires != null ? !nhisExpires.equals(that.nhisExpires) : that.nhisExpires != null) return false;
        if (patientType != that.patientType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (insured != null ? insured.hashCode() : 0);
        result = 31 * result + (nhis != null ? nhis.hashCode() : 0);
        result = 31 * result + (nhisExpires != null ? nhisExpires.hashCode() : 0);
        result = 31 * result + (mother != null ? mother.hashCode() : 0);
        result = 31 * result + (patientType != null ? patientType.hashCode() : 0);
        result = 31 * result + (estimateDateOfBirth != null ? estimateDateOfBirth.hashCode() : 0);
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        return result;
    }

    public static class Builder{
        private String id;
        private String firstName;
        private String middleName;
        private String lastName;
        private String prefferedName;
        private Date dateOfBirth;
        private String gender;
        private Boolean insured;
        private String nhis;
        private Date nhisExpires;
        private OpenMRSPatient mother;
        private PatientType patientType;
        private Boolean estimateDateOfBirth;
        private Facility facility;
        private String address;

        public OpenMRSPatient build(){
            return new OpenMRSPatient(this);
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder middleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder preferredName(String prefferedName) {
            this.prefferedName = prefferedName;
            return this;
        }

        public Builder dateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder insured(Boolean insured) {
            this.insured = insured;
            return this;
        }

        public Builder nhis(String nhis) {
            this.nhis = nhis;
            return this;
        }

        public Builder nhisExpires(Date nhisExpires) {
            this.nhisExpires = nhisExpires;
            return this;
        }

        public Builder mother(OpenMRSPatient mother) {
            this.mother = mother;
            return this;
        }

        public Builder patientType(PatientType patientType) {
            this.patientType = patientType;
            return this;
        }

        public Builder estimateDateOfBirth(Boolean estimateDateOfBirth) {
            this.estimateDateOfBirth = estimateDateOfBirth;
            return this;
        }

        public Builder facility(Facility facility) {
            this.facility = facility;
            return this;
        }
    }

}
