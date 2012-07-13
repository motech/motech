package org.motechproject.commcare.events.constants;

public final class EventSubjects {
    private static final String BASE_SUBJECT = "org.motechproject.commcare.api.";

    public static final String CASE_EVENT = BASE_SUBJECT + "case";

    public static final String MALFORMED_CASE_EXCEPTION = BASE_SUBJECT
            + "exception";

    private EventSubjects() {
    }
}
