package org.motechproject.commcare.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("create")
public class CreateElement {
    private String caseType;
    private String caseName;
    private String ownerId;

    public CreateElement(String caseType, String caseName, String ownerId) {
        this.caseType = caseType;
        this.caseName = caseName;
        this.ownerId = ownerId;
    }

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

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
