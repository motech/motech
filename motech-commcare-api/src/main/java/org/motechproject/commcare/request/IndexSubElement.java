package org.motechproject.commcare.request;

public class IndexSubElement {

    private String case_type;
    private String case_id;
    private String indexNodeName;

    public IndexSubElement(String caseId, String caseType, String indexNodeName) {
        this.case_id = caseId;
        this.case_type = caseType;
        this.indexNodeName = indexNodeName;
    }

    public String getCase_type() {
        return case_type;
    }

    public String getCase_id() {
        return case_id;
    }

    public String getIndexNodeName() {
        return indexNodeName;
    }
}
