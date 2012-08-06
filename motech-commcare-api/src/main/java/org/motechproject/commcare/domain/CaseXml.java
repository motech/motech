package org.motechproject.commcare.domain;

import java.util.HashMap;
import java.util.Map;

public class CaseXml {
    private String caseId;
    private String userId;
    private String apiKey;
    private String dateModified;
    private String action;
    private Map<String, String> fieldValues;
    private String caseType;
    private String caseName;
    private String ownerId;

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getUserId() {
        return this.userId;
    }

    public CaseXml() {
        this.fieldValues = new HashMap<String, String>();
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public void addFieldValue(String nodeName, String nodeValue) {
        this.fieldValues.put(nodeName, nodeValue);
    }

    public String getCaseId() {
        return this.caseId;
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public String getCaseType() {
        return this.caseType;
    }

    public String getCaseName() {
        return this.caseName;
    }

    public Map<String, String> getFieldValues() {
        return this.fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
