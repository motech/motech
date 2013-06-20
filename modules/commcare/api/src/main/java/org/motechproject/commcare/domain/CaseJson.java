package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;
import org.motechproject.commons.api.model.MotechProperties;

import java.util.List;
import java.util.Map;

public class CaseJson {

    @SerializedName("dated_closed")
    private String dateClosed;
    @SerializedName("domain")
    private String domain;
    @SerializedName("xform_ids")
    private List<String> xformIds;
    @SerializedName("version")
    private String version;
    @SerializedName("server_date_opened")
    private String serverDateOpened;
    @SerializedName("properties")
    private MotechProperties caseProperties;
    @SerializedName("server_date_modified")
    private String serverDateModified;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("date_modified")
    private String dateModified;
    @SerializedName("case_id")
    private String caseId;

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

    @SerializedName("closed")
    private boolean closed;

    public void setXformIds(List<String> xformIds) {
        this.xformIds = xformIds;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @SerializedName("indices")
    private Map<String, Map<String, String>> indices;

    public String getServerDateOpened() {
        return this.serverDateOpened;
    }

    public void setServerDateOpened(String serverDateOpened) {
        this.serverDateOpened = serverDateOpened;
    }

    public Map<String, String> getProperties() {
        return this.caseProperties;
    }

    public String getServerDateModified() {
        return this.serverDateModified;
    }

    public void setServerDateModified(String serverDateModified) {
        this.serverDateModified = serverDateModified;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getCaseId() {
        return this.caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
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
