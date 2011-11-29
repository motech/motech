package org.motechproject.mrs.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String securityRole;
    private String userName;

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

    public User userName(String userName) {
        this.userName = userName;
        return this;
    }

    public User securityRole(String securityRole) {
        this.securityRole = securityRole;
        return this;
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

    public String getFullName() {
        return firstName + " " + middleName + " " + lastName;
    }

    public String getUserName() {
        return userName;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public String getSecurityRole() {
        return securityRole;
    }

    public String getId() {
        return id;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
