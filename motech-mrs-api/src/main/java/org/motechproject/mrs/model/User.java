package org.motechproject.mrs.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String securityRole;
    private List<Attribute> attributes = new ArrayList<Attribute>();

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

    public String firstName() {
        return firstName;
    }

    public String middleName() {
        return middleName;
    }

    public String lastName() {
        return lastName;
    }

    public String fullName() {
        return firstName + " " + middleName + " " + lastName;
    }

    public List<Attribute> attributes() {
        return attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public String securityRole() {
        return securityRole;
    }

    public void securityRole(String securityRole) {
        this.securityRole = securityRole;
    }

    public String id() {
        return id;
    }
}
