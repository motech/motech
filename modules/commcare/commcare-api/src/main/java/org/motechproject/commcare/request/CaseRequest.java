package org.motechproject.commcare.request;

import org.motechproject.commcare.domain.IndexTask;
import org.motechproject.commcare.domain.UpdateTask;

public class CaseRequest {
    private CreateElement createElement;

    private UpdateTask updateElement;
    private CloseElement closeElement;
    private IndexTask indexElement;
    private String userId;
    private String xmlns;
    private String dataXmlns;
    private String dateModified;
    private String caseId;

    public CaseRequest(String caseId, String userId, String dateModified,
            String dataXmlns) {
        this.caseId = caseId;
        this.userId = userId;
        this.dateModified = dateModified;
        this.xmlns = "http://commcarehq.org/case/transaction/v2";
        this.dataXmlns = dataXmlns;
    }

    public String getDataXmlns() {
        return this.dataXmlns;
    }

    public void setDataXmlns(String dataXmlns) {
        this.dataXmlns = dataXmlns;
    }

    public void setIndexElement(IndexTask indexElement) {
        this.indexElement = indexElement;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getXmlns() {
        return this.xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public CaseRequest() {
        this.xmlns = "http://commcarehq.org/case/transaction/v2";
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public String getCaseId() {
        return this.caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public CreateElement getCreateElement() {
        return this.createElement;
    }

    public void setCreateElement(CreateElement createElement) {
        this.createElement = createElement;
    }

    public UpdateTask getUpdateElement() {
        return this.updateElement;
    }

    public void setUpdateElement(UpdateTask updateElement) {
        this.updateElement = updateElement;
    }

    public IndexTask getIndexElement() {
        return this.indexElement;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public CloseElement getCloseElement() {
        return this.closeElement;
    }

    public void setCloseElement(CloseElement closeElement) {
        this.closeElement = closeElement;
    }
}
