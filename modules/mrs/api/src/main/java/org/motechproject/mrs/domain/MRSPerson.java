package org.motechproject.mrs.domain;

import org.joda.time.DateTime;

import java.util.List;

public interface MRSPerson {

    String getPersonId();

    void setPersonId(String id);

    String getFirstName();

    void setFirstName(String firstName);

    String getMiddleName();

    void setMiddleName(String middleName);

    String getLastName();

    void setLastName(String lastName);

    String getPreferredName();

    void setPreferredName(String preferredName);

    String getAddress();

    void setAddress(String address);

    DateTime getDateOfBirth();

    void setDateOfBirth(DateTime dateOfBirth);

    Boolean getBirthDateEstimated();

    void setBirthDateEstimated(Boolean birthDateEstimated);

    Integer getAge();

    void setAge(Integer age);

    String getGender();

    void setGender(String gender);

    Boolean isDead();

    void setDead(Boolean dead);

    List<MRSAttribute> getAttributes();

    void setAttributes(List<MRSAttribute> attributes);

    DateTime getDeathDate();

    void setDeathDate(DateTime deathDate);

}
