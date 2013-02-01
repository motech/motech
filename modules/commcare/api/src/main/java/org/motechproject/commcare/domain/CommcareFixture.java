package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * A domain class that represents the information and properties of a fixture from
 * CommCareHQ.
 */
public class CommcareFixture {
    private String id;

    @SerializedName("fixture_type")
    private String fixtureType;

    private Map<String, String> fields;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFixtureType() {
        return fixtureType;
    }

    public void setFixtureType(String fixtureType) {
        this.fixtureType = fixtureType;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
