package org.motechproject.commcare.domain;

import java.util.List;
import java.util.Map;

/**
 * Wrapper class used for marshalling the JSON
 * responses from CommCare's cases API
 *
 */
public class CaseResponseJson {

    private String date_closed;
    private String domain;
    private List<String> xform_ids;
    private String version;
    private String server_date_opened;
    private Map<String, String> properties;
    private String server_date_modified;
    private String user_id;
    private String date_modified;
    private String case_id;
    private boolean closed;
    private Map<String, String> indices;

    public String getDate_closed() {
        return date_closed;
    }
    public String getDomain() {
        return domain;
    }
    public List<String> getXform_ids() {
        return xform_ids;
    }
    public String getVersion() {
        return version;
    }
    public String getServer_date_opened() {
        return server_date_opened;
    }
    public Map<String, String> getProperties() {
        return properties;
    }
    public String getServer_date_modified() {
        return server_date_modified;
    }
    public String getUser_id() {
        return user_id;
    }
    public String getDate_modified() {
        return date_modified;
    }
    public String getCase_id() {
        return case_id;
    }
    public boolean isClosed() {
        return closed;
    }
    public Map<String, String> getIndices() {
        return indices;
    }



}
