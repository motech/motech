package org.motechproject.mrs.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;

/**
 * Domain to hold personal details of a Person (MRSUser and MRSPatient)
 */
public class OpenMRSPerson implements Person {

    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private String address;
    private DateTime dateOfBirth;
    private Boolean birthDateEstimated;
    private Integer age;
    private String gender;
    private boolean dead;
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private DateTime deathDate;

    public OpenMRSPerson preferredName(String preferredName) {
        this.preferredName = preferredName;
        return this;
    }

    public OpenMRSPerson age(Integer age) {
        this.age = age;
        return this;
    }

    public OpenMRSPerson address(String address) {
        this.address = address;
        return this;
    }

    public OpenMRSPerson dateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public OpenMRSPerson birthDateEstimated(Boolean birthDateEstimated) {
        this.birthDateEstimated = birthDateEstimated;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public OpenMRSPerson gender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getId() {
        return id;
    }

    public OpenMRSPerson id(String id) {
        this.id = id;
        return this;
    }

    public OpenMRSPerson firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public OpenMRSPerson middleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public OpenMRSPerson lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public OpenMRSPerson dead(Boolean dead) {
        this.dead = dead;
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

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public OpenMRSPerson addAttribute(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }

    public OpenMRSPerson attributes(List<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public OpenMRSPerson deathDate(DateTime deathDate) {
        this.deathDate = deathDate;
        return this;
    }

    public String attrValue(String key) {
        List<Attribute> filteredItems = select(attributes, having(on(Attribute.class).getName(), equalTo(key)));
        return CollectionUtils.isNotEmpty(filteredItems) ? filteredItems.get(0).getValue() : null;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public DateTime getDateOfBirth() {
        return new DateTime(dateOfBirth);
    }

    public String getAddress() {
        return address;
    }

    public Boolean getBirthDateEstimated() {
        return birthDateEstimated;
    }

    public Boolean isDead() {
        return dead;
    }

    public DateTime deathDate() {
        return deathDate;
    }

    public Integer getAge() {
        return age;
    }

    public DateTime getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(DateTime deathDate) {
        this.deathDate = deathDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setBirthDateEstimated(Boolean birthDateEstimated) {
        this.birthDateEstimated = birthDateEstimated;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OpenMRSPerson)) {
            return false;
        }

        OpenMRSPerson other = (OpenMRSPerson) o;

        return equalNameData(other) && equalAgeAndBirthDates(other) && Objects.equals(id, other.id)
                && Objects.equals(address, other.address) && Objects.equals(gender, other.gender)
                && Objects.equals(attributes, other.attributes) && Objects.equals(deathDate, other.deathDate)
                && dead == other.dead;
    }

    public boolean equalNameData(OpenMRSPerson other) {
        return Objects.equals(firstName, other.firstName) && Objects.equals(middleName, other.middleName)
                && Objects.equals(lastName, other.lastName) && Objects.equals(preferredName, other.preferredName);
    }

    public boolean equalAgeAndBirthDates(OpenMRSPerson other) {
        return Objects.equals(dateOfBirth, other.dateOfBirth)
                && Objects.equals(birthDateEstimated, other.birthDateEstimated) && Objects.equals(age, other.age);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(id);
        hash = hash * 31 + ObjectUtils.hashCode(firstName);
        hash = hash * 31 + ObjectUtils.hashCode(middleName);
        hash = hash * 31 + ObjectUtils.hashCode(lastName);
        hash = hash * 31 + ObjectUtils.hashCode(preferredName);
        hash = hash * 31 + ObjectUtils.hashCode(address);
        hash = hash * 31 + ObjectUtils.hashCode(dateOfBirth);
        hash = hash * 31 + ObjectUtils.hashCode(birthDateEstimated);
        hash = hash * 31 + ObjectUtils.hashCode(age);
        hash = hash * 31 + ObjectUtils.hashCode(gender);
        hash = hash * 31 + Boolean.valueOf(dead).hashCode();
        hash = hash * 31 + ObjectUtils.hashCode(attributes);
        hash = hash * 31 + ObjectUtils.hashCode(deathDate);
        return hash;
    }

    @Override
    public String getPersonId() {
        return id;
    }

    @Override
    public void setPersonId(String id) {
        this.id = id;
    }
}
