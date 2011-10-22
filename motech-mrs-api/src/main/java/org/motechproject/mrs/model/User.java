package org.motechproject.mrs.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<UserAttribute> attributes = new ArrayList<UserAttribute>();

    public User id(String id) {
        this.id = id;
        return this;
    }

    public User firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User middleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public User lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public User phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}
