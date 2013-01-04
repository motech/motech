package org.motechproject.couch.mrs.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import static org.motechproject.commons.date.util.DateUtil.setTimeZone;

@TypeDiscriminator("doc.type === 'MRSPerson'")
public class CouchMRSPerson extends MotechBaseDataObject {

    private final String type = "MRSPerson";

    @JsonProperty
    private String externalId;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String address;
    @JsonProperty
    private DateTime dateOfBirth;
    @JsonProperty
    private String gender;
    @JsonProperty
    private List<Attribute> attributes = new ArrayList<Attribute>();

    public CouchMRSPerson() {
        super();
        this.setType(type);
    }

    public CouchMRSPerson(String externalId, String firstName, String lastName, String address, DateTime dateOfBirth,
            String gender, List<Attribute> attributes) {
        super();
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.attributes = attributes;
        this.setType(type);
    }

    public String getAddress() {
        return address;
    }

    public CouchMRSPerson setAddress(String address) {
        this.address = address;
        return this;
    }

    public DateTime getDateOfBirth() {
        return setTimeZone(dateOfBirth);
    }

    public CouchMRSPerson setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public CouchMRSPerson setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public CouchMRSPerson setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public CouchMRSPerson setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public CouchMRSPerson setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public CouchMRSPerson addAttribute(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }

    public CouchMRSPerson setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String attrValue(String key) {
        List<Attribute> atts = new ArrayList<Attribute>();
        for (Attribute att : attributes) {
            if ((att.getName().matches(key))) {
                atts.add(att);
            }
        }
        return CollectionUtils.isNotEmpty(atts) ? atts.get(0).getValue() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CouchMRSPerson)) {
            return false;
        }

        CouchMRSPerson other = (CouchMRSPerson) o;

        return Objects.equals(externalId, other.externalId) && Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName) && Objects.equals(address, other.address) &&
                Objects.equals(getDateOfBirth(), other.getDateOfBirth()) && Objects.equals(gender, other.gender) &&
                Objects.equals(attributes, other.attributes);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(externalId);
        hash = hash * 31 + ObjectUtils.hashCode(firstName);
        hash = hash * 31 + ObjectUtils.hashCode(lastName);
        hash = hash * 31 + ObjectUtils.hashCode(address);
        hash = hash * 31 + ObjectUtils.hashCode(dateOfBirth);
        hash = hash * 31 + ObjectUtils.hashCode(gender);
        hash = hash * 31 + ObjectUtils.hashCode(attributes);
        return hash;
    }
}
