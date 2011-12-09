package org.motechproject.mrs.model;

import java.util.Date;
import java.util.List;

public class Patient {

    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private Date dateOfBirth;
    private String gender;
    private String address;
    private MRSFacility facility;
    private Boolean birthDateEstimated;
    private List<Attribute> attributes;


    public Patient(String id, String firstName, String middleName, String lastName, String preferredName, Date dateOfBirth, Boolean birthDateEstimated, String gender, String address, List<Attribute> attributes, MRSFacility facility) {
        this(id, firstName, middleName, lastName, preferredName, dateOfBirth, birthDateEstimated, gender, address, facility);
        this.attributes = attributes;
    }
    public Patient(String firstName, String middleName, String lastName, String preferredName, Date dateOfBirth, Boolean birthDateEstimated, String gender, String address, MRSFacility facility) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.facility = facility;
        this.birthDateEstimated = birthDateEstimated;
    }

    public Patient(String id, String firstName, String middleName, String lastName, String preferredName, Date dateOfBirth, Boolean birthDateEstimated, String gender, String address, MRSFacility facility) {
        this(firstName, middleName, lastName, preferredName, dateOfBirth, birthDateEstimated, gender, address, facility);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public MRSFacility getFacility() {
        return facility;
    }

    public Boolean getBirthDateEstimated() {
        return birthDateEstimated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;

        Patient patient = (Patient) o;

        if (address != null ? !address.equals(patient.address) : patient.address != null) return false;
        if (attributes != null ? !attributes.equals(patient.attributes) : patient.attributes != null) return false;
        if (birthDateEstimated != null ? !birthDateEstimated.equals(patient.birthDateEstimated) : patient.birthDateEstimated != null)
            return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(patient.dateOfBirth) : patient.dateOfBirth != null) return false;
        if (facility != null ? !facility.equals(patient.facility) : patient.facility != null) return false;
        if (firstName != null ? !firstName.equals(patient.firstName) : patient.firstName != null) return false;
        if (gender != null ? !gender.equals(patient.gender) : patient.gender != null) return false;
        if (id != null ? !id.equals(patient.id) : patient.id != null) return false;
        if (lastName != null ? !lastName.equals(patient.lastName) : patient.lastName != null) return false;
        if (middleName != null ? !middleName.equals(patient.middleName) : patient.middleName != null) return false;
        if (preferredName != null ? !preferredName.equals(patient.preferredName) : patient.preferredName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (preferredName != null ? preferredName.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        result = 31 * result + (birthDateEstimated != null ? birthDateEstimated.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }
}
