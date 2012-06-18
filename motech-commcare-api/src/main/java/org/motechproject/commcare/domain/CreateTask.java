package org.motechproject.commcare.domain;

/**
 * Class that represents a create element in
 * case XML. Inclusion of this object into
 * the CaseTask will generate a create element
 * in the XML. Case type and case name are
 * mandatory fields.
 *
 */
public class CreateTask {

    private String caseType;
    private String ownerId;
    private String caseName;

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }




}
