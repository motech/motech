package org.motechproject.commcare.events.events;

import org.motechproject.commcare.domain.CaseXml;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.scheduler.domain.MotechEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * An event class for representing changes to a case
 * which MoTeCH was informed of by CommCare. This class
 * is converted to a MoTeCH event and raised. How much
 * data is included in the MoTeCH event and sent to the
 * queue depends on the strategy outlined in the
 * caseEventStrategy properties file. The full strategy
 * will shuttle all data, including custom data. The partial
 * strategy will shuttle all data other than custom data.
 * The minimal strategy will only shuttle the case ID.
 *
 */

public class CaseEvent {

    private String case_id;
    private String user_id;
    private String api_key;
    private String date_modified;
    private String action;
    private Map<String,String> fieldValues;
    private String case_type;
    private String case_name;
    private String owner_id;

    public CaseEvent(String case_id) {
        this.case_id = case_id;
    }

    public CaseEvent(MotechEvent event) {
        this.case_id = (String) event.getParameters().get(EventDataKeys.CASE_ID);
        this.user_id = (String) event.getParameters().get(EventDataKeys.USER_ID);
        this.api_key = (String) event.getParameters().get(EventDataKeys.API_KEY);
        this.date_modified = (String) event.getParameters().get(EventDataKeys.DATE_MODIFIED);
        this.action = (String) event.getParameters().get(EventDataKeys.CASE_ACTION);
        this.fieldValues = (Map<String, String>) event.getParameters().get(EventDataKeys.FIELD_VALUES);
        this.case_type = (String) event.getParameters().get(EventDataKeys.CASE_TYPE);
        this.case_name = (String) event.getParameters().get(EventDataKeys.CASE_NAME);
        this.owner_id = (String) event.getParameters().get(EventDataKeys.OWNER_ID);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getDate_modified() {
        return date_modified;
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

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
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

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public void setCaseId(String caseId) {
        this.case_id = caseId;
    }

    public String getCaseId() {
        return case_id;
    }

    public MotechEvent toMotechEventWithoutData() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKeys.CASE_ID, case_id);
        return new MotechEvent(EventSubjects.CASE_EVENT, parameters);
    }

    public MotechEvent toMotechEventWithData() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKeys.CASE_ID, case_id);
        parameters.put(EventDataKeys.USER_ID, user_id);
        parameters.put(EventDataKeys.CASE_ACTION, action);
        parameters.put(EventDataKeys.API_KEY, api_key);
        parameters.put(EventDataKeys.CASE_NAME, case_name);
        parameters.put(EventDataKeys.CASE_TYPE, case_type);
        parameters.put(EventDataKeys.DATE_MODIFIED, date_modified);
        parameters.put(EventDataKeys.OWNER_ID, owner_id);
        return new MotechEvent(EventSubjects.CASE_EVENT, parameters);
    }

    public CaseEvent eventFromCase(CaseXml caseInstance) {
        this.setCaseId(caseInstance.getCase_id());
        this.setUser_id(caseInstance.getUser_id());
        this.setAction(caseInstance.getAction());
        this.setApi_key(caseInstance.getApi_key());
        this.setCase_name(caseInstance.getCase_name());
        this.setCase_type(caseInstance.getCase_type());
        this.setDate_modified(caseInstance.getDate_modified());
        this.setFieldValues(caseInstance.getFieldValues());
        this.setOwner_id(caseInstance.getOwner_id());

        return this;
    }


}
