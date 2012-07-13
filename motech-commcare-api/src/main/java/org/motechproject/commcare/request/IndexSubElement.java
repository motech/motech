package org.motechproject.commcare.request;

/**
 * Domain class that is converted to indices in the index element of the case
 * xml.
 */
public class IndexSubElement {
    private String caseType;
    private String caseId;
    private String indexNodeName;

    public IndexSubElement(String caseId, String caseType, String indexNodeName) {
        this.caseId = caseId;
        this.caseType = caseType;
        this.indexNodeName = indexNodeName;
    }

    public String getCaseType() {
        return this.caseType;
    }

    public String getCaseId() {
        return this.caseId;
    }

    public String getIndexNodeName() {
        return this.indexNodeName;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setIndexNodeName(String indexNodeName) {
        this.indexNodeName = indexNodeName;
    }

}
