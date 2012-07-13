package org.motechproject.commcare.domain;

import java.util.Map;

/**
 * A domain class to include in a case task in order to generate an upload case
 * block in case xml.
 */
public class UpdateTask {
    private String caseType;
    private String caseName;
    private String dateOpened;
    private String ownerId;
    private Map<String, String> fieldValues;

    public String getCaseType() {
        return this.caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseName() {
        return this.caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getDateOpened() {
        return this.dateOpened;
    }

    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Map<String, String> getFieldValues() {
        return this.fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }
}
