package org.motechproject.commcare.request;

import java.util.HashMap;
import java.util.Map;

import org.motechproject.commcare.request.converter.UpdateElementConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class UpdateElement {

    private String case_type;
    private String case_name;
    private String date_opened;
    private String owner_id;

    private Map<String, String> fieldValues = new HashMap<String, String>();

    public UpdateElement(String case_type, String case_name, String date_opened, String owner_id, Map<String, String> fieldValues) {
        this.case_type = case_type;
        this.case_name = case_name;
        this.date_opened = date_opened;
        this.owner_id = owner_id;
        this.fieldValues = fieldValues;
    }

    public String getCase_type() {
        return case_type;
    }

    public void setCase_type(String case_type) {
        this.case_type = case_type;
    }

    public String getCase_name() {
        return case_name;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
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

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

}
