package org.motechproject.commcare.domain;

import java.util.List;
import java.util.Map;

/**
 * Domain class that represents the information and properties of a user on
 * CommCareHQ.
 */
public class CaseInfo {

    private String caseId;
    private String userId;
    private String dateClosed;
    private String domain;
    private List<String> xformIds;
    private String version;
    private String serverDateOpened;
    private String caseType;
    private String dateOpened;
    private String ownerId;
    private String caseName;
    private Map<String, String> fieldValues;
    private String serverDateModified;
    private boolean closed;
    private Map<String, Map<String, String>> indices;

    public String getCaseId() {
        return this.caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateClosed() {
        return this.dateClosed;
    }

    public void setDateClosed(String dateClosed) {
        this.dateClosed = dateClosed;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getXformIds() {
        return this.xformIds;
    }

    public void setXformIds(List<String> xformIds) {
        this.xformIds = xformIds;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServerDateOpened() {
        return this.serverDateOpened;
    }

    public void setServerDateOpened(String serverDateOpened) {
        this.serverDateOpened = serverDateOpened;
    }

    public String getCaseType() {
        return this.caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
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

    public String getCaseName() {
        return this.caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public Map<String, String> getFieldValues() {
        return this.fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public String getServerDateModified() {
        return this.serverDateModified;
    }

    public void setServerDateModified(String serverDateModified) {
        this.serverDateModified = serverDateModified;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Map<String, Map<String, String>> getIndices() {
        return this.indices;
    }

    public void setIndices(Map<String, Map<String, String>> indices) {
        this.indices = indices;
    }
}
