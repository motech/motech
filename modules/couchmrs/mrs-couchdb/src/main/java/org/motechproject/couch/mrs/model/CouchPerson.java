package org.motechproject.couch.mrs.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.couch.mrs.util.CouchAttributeDeserializer;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.motechproject.commons.date.util.DateUtil.setTimeZone;

@TypeDiscriminator("doc.type === 'Person'")
public class CouchPerson extends MotechBaseDataObject implements MRSPerson {

    private static final long serialVersionUID = 1L;

    private final String type = "Person";

    @JsonProperty
    private String personId;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String middleName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String preferredName;
    @JsonProperty
    private String address;
    @JsonProperty
    private DateTime dateOfBirth;
    @JsonProperty
    private Boolean birthDateEstimated;
    @JsonProperty
    private Integer age;
    @JsonProperty
    private String gender;
    @JsonProperty
    private Boolean dead;
    @JsonProperty
    @JsonDeserialize(using = CouchAttributeDeserializer.class)
    private List<MRSAttribute> attributes = new ArrayList<MRSAttribute>();
    @JsonProperty
    private DateTime deathDate;

    public CouchPerson() {
        super();
        this.setType(type);
    }

    public CouchPerson(String personId, String firstName, String middleName, String lastName, String preferredName, String address,
                       DateTime dateOfBirth, Boolean birthDateEstimated, Integer age, String gender, Boolean dead,
                       List<MRSAttribute> attributes, DateTime deathDate) {
        super();
        this.setType(type);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.birthDateEstimated = birthDateEstimated;
        this.age = age;
        this.gender = gender;
        this.dead = dead;
        this.attributes = attributes;
        this.deathDate = deathDate;
        if (personId != null) {
            setId(personId);
        }
    }

    public String getPersonId() {
        return getId();
    }

    public void setPersonId(String personId) {
        if (personId != null) {
            setId(personId);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DateTime getDateOfBirth() {
        return setTimeZone(dateOfBirth);
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getBirthDateEstimated() {
        return birthDateEstimated;
    }

    public void setBirthDateEstimated(Boolean birthDateEstimated) {
        this.birthDateEstimated = birthDateEstimated;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean isDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public List<MRSAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<MRSAttribute> attributes) {
        this.attributes = attributes;
    }

    public DateTime getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(DateTime deathDate) {
        this.deathDate = deathDate;
    }

    public String attrValue(String key) {
        List<MRSAttribute> atts = new ArrayList<MRSAttribute>();
        for (MRSAttribute att : attributes) {
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

        if (!(o instanceof CouchPerson)) {
            return false;
        }

        CouchPerson other = (CouchPerson) o;

        return Objects.equals(getId(), other.getPersonId()) && Objects.equals(firstName, other.getFirstName())
                && Objects.equals(lastName, other.getLastName()) && Objects.equals(address, other.getAddress())
                && Objects.equals(getDateOfBirth(), other.getDateOfBirth()) && Objects.equals(gender, other.getGender())
                && Objects.equals(attributes, other.getAttributes());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(personId);
        hash = hash * 31 + ObjectUtils.hashCode(firstName);
        hash = hash * 31 + ObjectUtils.hashCode(lastName);
        hash = hash * 31 + ObjectUtils.hashCode(address);
        hash = hash * 31 + ObjectUtils.hashCode(dateOfBirth);
        hash = hash * 31 + ObjectUtils.hashCode(gender);
        hash = hash * 31 + ObjectUtils.hashCode(attributes);
        return hash;
    }

    public void addAttribute(CouchAttribute attribute) {
        attributes.add(attribute);
    }

    @Override
    public String toString() {
        return "CouchPerson{" +
                "type='" + type + '\'' +
                ", personId='" + getPersonId() + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", preferredName='" + preferredName + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + getDateOfBirth() +
                ", birthDateEstimated=" + birthDateEstimated +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", dead=" + dead +
                ", attributes=" + attributes +
                ", deathDate=" + deathDate +
                '}';
    }
}
