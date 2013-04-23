package org.motechproject.commcare.events.constants;

public final class EventSubjects {
    private static final String BASE_SUBJECT = "org.motechproject.commcare.api.";

    public static final String CASE_EVENT = BASE_SUBJECT + "case";

    public static final String MALFORMED_CASE_EXCEPTION = BASE_SUBJECT
            + "exception";

    public static final String FORM_STUB_EVENT = BASE_SUBJECT + "formstub";

    public static final String FORM_STUB_FAIL_EVENT = FORM_STUB_EVENT + ".failed";

    public static final String FORMS_EVENT = BASE_SUBJECT + "forms";

    public static final String FORMS_FAIL_EVENT = FORMS_EVENT + ".failed";

    private EventSubjects() {
    }
}
