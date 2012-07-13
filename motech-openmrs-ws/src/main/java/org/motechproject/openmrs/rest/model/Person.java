package org.motechproject.openmrs.rest.model;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Person {
    private String uuid;
    private String display;
    private String gender;
    private Integer age;
    private Date birthdate;
    private boolean birthdateEstimated;
    private boolean dead;
    private Concept causeOfDeath;
    private Date deathDate;

    private PreferredName preferredName;
    private List<PreferredName> names;

    private PreferredAddress preferredAddress;
    private List<PreferredAddress> addresses;

    private List<Attribute> attributes;

    public static class PersonSerializer implements JsonSerializer<Person> {
        @Override
        public JsonElement serialize(Person src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getUuid());
        }
    }

    public static class PreferredName {
        private String uuid;
        private String display;
        private String givenName;
        private String middleName;
        private String familyName;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public String getGivenName() {
            return givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }
    }

    public static class PreferredAddress {
        private String uuid;
        private String address1;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isBirthdateEstimated() {
        return birthdateEstimated;
    }

    public void setBirthdateEstimated(boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public Concept getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(Concept causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public PreferredName getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(PreferredName preferredName) {
        this.preferredName = preferredName;
    }

    public PreferredAddress getPreferredAddress() {
        return preferredAddress;
    }

    public void setPreferredAddress(PreferredAddress preferredAddress) {
        this.preferredAddress = preferredAddress;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<PreferredName> getNames() {
        return names;
    }

    public void setNames(List<PreferredName> names) {
        this.names = names;
    }

    public List<PreferredAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PreferredAddress> addresses) {
        this.addresses = addresses;
    }
}
