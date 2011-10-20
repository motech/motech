package org.motechproject.mrs.model;

import java.util.Date;

public class Patient {

    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String prefferedName;
    private Date dateOfBirth;
    private String gender;
    private String address;
    private String phoneNumber;

    public Patient() {
    }

    public Patient(String firstName, String middleName, String lastName, String prefferedName, Date dateOfBirth, String gender, String address, String phoneNumber) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.prefferedName = prefferedName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Patient(String id, String firstName, String middleName, String lastName, String prefferedName, Date dateOfBirth, String gender, String address, String phoneNumber) {
        this(firstName, middleName, lastName, prefferedName, dateOfBirth, gender, address, phoneNumber);
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

    public String getPrefferedName() {
        return prefferedName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
