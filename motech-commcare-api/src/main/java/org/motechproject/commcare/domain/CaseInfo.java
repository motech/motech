package org.motechproject.commcare.domain;

import java.util.List;
import java.util.Map;
/**
 * An entity class that represents that information
 * about a case provided by CommCareHQ's cases API
 */
public class CaseInfo {

    private String case_id;
    private String user_id;
    private String date_closed;
    private String domain;
    private List<String> xform_ids;
    private String version;
    private String server_date_opened;
    private String case_type;
    private String date_opened;
    private String owner_id;
    private String case_name;
    private Map<String, String> field_values;
    private String server_date_modified;
    private boolean closed;
    private Map<String, String> indices;

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_closed() {
        return date_closed;
    }

    public void setDate_closed(String date_closed) {
        this.date_closed = date_closed;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getXform_ids() {
        return xform_ids;
    }

    public void setXform_ids(List<String> xform_ids) {
        this.xform_ids = xform_ids;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServer_date_opened() {
        return server_date_opened;
    }

    public void setServer_date_opened(String server_date_opened) {
        this.server_date_opened = server_date_opened;
    }

    public String getCase_type() {
        return case_type;
    }

    public void setCase_type(String case_type) {
        this.case_type = case_type;
    }

    public String getDate_opened() {
        return date_opened;
    }

    public void setDate_opened(String date_opened) {
        this.date_opened = date_opened;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getCase_name() {
        return case_name;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }

    public Map<String, String> getField_values() {
        return field_values;
    }

    public void setField_values(Map<String, String> field_values) {
        this.field_values = field_values;
    }

    public String getServer_date_modified() {
        return server_date_modified;
    }

    public void setServer_date_modified(String server_date_modified) {
        this.server_date_modified = server_date_modified;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Map<String, String> getIndices() {
        return indices;
    }

    public void setIndices(Map<String, String> indices) {
        this.indices = indices;
    }


}
