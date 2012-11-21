package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class CommcareForm {

    private FormValueElement form;
    private FormValueElement caseElement;
    private String id;
    private String md5;
    private Map<String, String> metadata; // change this to a metadata class
    @SerializedName("resource_uri")
    private String resourceUri;
    private String type;
    private String uiversion;
    private String version;

    public FormValueElement getForm() {
        return form;
    }
    public void setForm(FormValueElement form) {
        this.form = form;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMd5() {
        return md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public Map<String, String> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    public String getResourceUri() {
        return resourceUri;
    }
    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUiversion() {
        return uiversion;
    }
    public void setUiversion(String uiversion) {
        this.uiversion = uiversion;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public FormValueElement getCaseElement() {
        return caseElement;
    }

    public void setCaseElement(FormValueElement caseElement) {
        this.caseElement = caseElement;
    }
}
