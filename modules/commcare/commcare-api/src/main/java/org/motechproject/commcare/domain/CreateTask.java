package org.motechproject.commcare.domain;

/**
 * A domain class to include in a CaseTask in order to generate a create block.
 * Create blocks must have a case type and case name to be valid case XML for
 * uploading to CommCareHQ.
 */
public class CreateTask {
    private String caseType;
    private String ownerId;
    private String caseName;

    public String getCaseType() {
        return this.caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
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
}
