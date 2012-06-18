package org.motechproject.commcare.domain;
/**
 * Class for unmarshalling the XML posted to
 * MoTeCH by CommCareHQ's data forwarding
 */
import java.util.HashMap;
import java.util.Map;

public class CaseXml {
    private String case_id;
    private String user_id;
    private String api_key;
    private String date_modified;

    private String action;
    private Map<String,String> fieldValues;
    private String case_type;
    private String case_name;

    private String owner_id;

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public CaseXml(){
        fieldValues = new HashMap<String, String>();
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setCase_type(String case_type) {
        this.case_type = case_type;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }

    public void AddFieldvalue(String nodeName, String nodeValue) {
        fieldValues.put(nodeName,nodeValue);
    }

    public String getCase_id() {
        return case_id;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public String getCase_type() {
        return case_type;
    }

    public String getCase_name() {
        return case_name;
    }

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public void setUser_id(String userId) {
        this.user_id =   userId;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }
}
