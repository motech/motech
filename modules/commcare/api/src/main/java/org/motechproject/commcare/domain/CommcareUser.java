package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * A domain class that represents the information and properties of a user from
 * CommCareHQ.
 */
public class CommcareUser {
    @SerializedName("default_phone_number")
    private String defaultPhoneNumber;
    private String email;
    @SerializedName("first_name")
    private String firstName;
    private String id;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("resource_ui")
    private String resourceUi;
    private String username;
    private List<String> groups;
    @SerializedName("user_data")
    private Map<String, String> userData;
    @SerializedName("phone_numbers")
    private List<String> phoneNumbers;

    public String getDefaultPhoneNumber() {
        return this.defaultPhoneNumber;
    }

    public void setDefaultPhoneNumber(String defaultPhoneNumber) {
        this.defaultPhoneNumber = defaultPhoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getResourceUi() {
        return this.resourceUi;
    }

    public void setResourceUi(String resourceUi) {
        this.resourceUi = resourceUi;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getGroups() {
        return this.groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public Map<String, String> getUserData() {
        return this.userData;
    }

    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }

    public List<String> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
