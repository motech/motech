package org.motechproject.mrs.model;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

public class MRSUser {
    private String id;
    private String systemId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String securityRole;
    private String userName;
    private List<Attribute> attributes = new ArrayList<Attribute>();

    public MRSUser id(String id) {
        this.id = id;
        return this;
    }

    public MRSUser firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public MRSUser middleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public MRSUser lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public MRSUser userName(String userName) {
        this.userName = userName;
        return this;
    }

    public MRSUser securityRole(String securityRole) {
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


    public MRSUser addAttribute(Attribute attribute) {
        attributes.add(attribute);
        return this;
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

    public String getSystemId() {
        return systemId;
    }

    public MRSUser systemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

     public String attrValue(String key) {
        List<Attribute> filteredItems = select(attributes, having(on(Attribute.class).name(), equalTo(key)));
        return CollectionUtils.isNotEmpty(filteredItems) ? filteredItems.get(0).value() : null;
    }
}
