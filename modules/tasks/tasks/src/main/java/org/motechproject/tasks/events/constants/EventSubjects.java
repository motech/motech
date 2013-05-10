package org.motechproject.tasks.events.constants;

public final class EventSubjects {
    private EventSubjects() {
    }

    public static final String BASE_SUBJECT = "org.motechproject.tasks.";

    public static final String HANDLER_SUBJECT = BASE_SUBJECT + "handler.";
    public static final String ACTION_FAILED_SUBJECT = HANDLER_SUBJECT + "action.failed";
    public static final String TRIGGER_FAILED_SUBJECT = HANDLER_SUBJECT + "trigger.failed";

    public static final String CHANNEL_SUBJECT = BASE_SUBJECT + "channel.";
    public static final String CHANNEL_UPDATE_SUBJECT = CHANNEL_SUBJECT + "update";

    public static final String DATA_PROVIDER_SUBJECT = BASE_SUBJECT + "dataProvider.";
    public static final String DATA_PROVIDER_UPDATE_SUBJECT = DATA_PROVIDER_SUBJECT + "update";
}
