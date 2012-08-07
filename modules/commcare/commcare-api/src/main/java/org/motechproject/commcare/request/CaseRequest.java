package org.motechproject.commcare.request;

import org.motechproject.commcare.domain.IndexTask;
import org.motechproject.commcare.domain.UpdateTask;

public class CaseRequest {
    private CreateElement createElement;

    private UpdateTask updateElement;
    private CloseElement closeElement;
    private IndexTask indexElement;
    private String user_id;
    private String xmlns;
    private String dataXmlns;
    private String date_modified;
    private String case_id;

    public CaseRequest(String caseId, String userId, String dateModified,
            String dataXmlns) {
        this.case_id = caseId;
        this.user_id = userId;
        this.date_modified = dateModified;
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

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
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

    public String getDate_modified() {
        return this.date_modified;
    }

    public String getCase_id() {
        return this.case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
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

    public void setUser_id(String userId) {
        this.user_id = userId;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public CloseElement getCloseElement() {
        return this.closeElement;
    }

    public void setCloseElement(CloseElement closeElement) {
        this.closeElement = closeElement;
    }
}
