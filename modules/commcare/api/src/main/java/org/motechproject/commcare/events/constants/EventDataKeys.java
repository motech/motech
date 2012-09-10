package org.motechproject.commcare.events.constants;

public final class EventDataKeys {

    //CaseEvent
    public static final String CASE_ID = "caseId";
    public static final String USER_ID = "userId";
    public static final String API_KEY = "apiKey";
    public static final String DATE_MODIFIED = "dateModified";
    public static final String CASE_ACTION = "caseAction";
    public static final String FIELD_VALUES = "fieldValues";
    public static final String CASE_TYPE = "caseType";
    public static final String CASE_NAME = "caseName";
    public static final String OWNER_ID = "ownerId";

    //Malformed case xml exception event
    public static final String MESSAGE = "message";

    //FormStubEvent
    public static final String RECEIVED_ON = "receivedOn";
    public static final String FORM_ID = "formId";
    public static final String CASE_IDS = "caseIds";

    private EventDataKeys() {
    }
}
