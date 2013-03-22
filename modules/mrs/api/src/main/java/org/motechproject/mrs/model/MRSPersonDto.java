package org.motechproject.mrs.model;


import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.now;

public class MRSPersonDto implements MRSPerson {
    private String personId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private String address;
    private DateTime dateOfBirth;
    private Boolean birthDateEstimated;
    private Integer age;
    private String gender;
    private Boolean dead;
    @JsonDeserialize(contentAs = MRSAttributeDto.class)
    private List<MRSAttribute> attributes = new ArrayList<MRSAttribute>();
    private DateTime deathDate;

    public MRSPersonDto() {
    }

    public MRSPersonDto(String personId, String firstName, String middleName, String lastName, String preferredName, String address, DateTime dateOfBirth, Boolean birthDateEstimated, Integer age, String gender, boolean dead, List<MRSAttribute> attributes, DateTime deathDate) {
        this.personId = personId;
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
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
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
        return dateOfBirth;
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
        if (this.dateOfBirth != null) {
            this.age = Years.yearsBetween(this.dateOfBirth, new DateTime(now())).getYears();
        }
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
}
