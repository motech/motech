package org.motechproject.mrs.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;

/**
 * Domain to hold personal details of a Person (MRSUser and MRSPatient)
 */
public class MRSPerson {

    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private String address;
    private Date dateOfBirth;
    private Boolean birthDateEstimated;
    private Integer age;
    private String gender;
    private boolean dead;
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private Date deathDate;

    public MRSPerson preferredName(String preferredName) {
        this.preferredName = preferredName;
        return this;
    }

    public MRSPerson age(Integer age) {
        this.age = age;
        return this;
    }

    public MRSPerson address(String address) {
        this.address = address;
        return this;
    }

    public MRSPerson dateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public MRSPerson birthDateEstimated(Boolean birthDateEstimated) {
        this.birthDateEstimated = birthDateEstimated;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public MRSPerson gender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getId() {
        return id;
    }

    public MRSPerson id(String id) {
        this.id = id;
        return this;
    }

    public MRSPerson firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public MRSPerson middleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public MRSPerson lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public MRSPerson dead(Boolean dead) {
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

    public MRSPerson addAttribute(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }

    public MRSPerson attributes(List<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public MRSPerson deathDate(Date deathDate) {
        this.deathDate = deathDate;
        return this;
    }

    public String attrValue(String key) {
        List<Attribute> filteredItems = select(attributes, having(on(Attribute.class).name(), equalTo(key)));
        return CollectionUtils.isNotEmpty(filteredItems) ? filteredItems.get(0).value() : null;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
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

    public Date deathDate() {
        return deathDate;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MRSPerson)) {
            return false;
        }

        MRSPerson other = (MRSPerson) o;

        return equalNameData(other) && equalAgeAndBirthDates(other) && Objects.equals(id, other.id) &&
                Objects.equals(address, other.address) && Objects.equals(gender, other.gender) &&
                Objects.equals(attributes, other.attributes) && Objects.equals(deathDate, other.deathDate) &&
                dead == other.dead;
    }

    public boolean equalNameData(MRSPerson other) {
        return Objects.equals(firstName, other.firstName) &&
                Objects.equals(middleName, other.middleName) && Objects.equals(lastName, other.lastName) &&
                Objects.equals(preferredName, other.preferredName);
    }

    public boolean equalAgeAndBirthDates(MRSPerson other) {
        return Objects.equals(dateOfBirth, other.dateOfBirth) &&
                Objects.equals(birthDateEstimated, other.birthDateEstimated) && Objects.equals(age, other.age);
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
}
