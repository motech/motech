package org.motechproject.tasks.events.constants;

public final class EventSubjects {
    private EventSubjects() {
    }

    public static final String BASE_SUBJECT = "org.motechproject.tasks.";

    public static final String CHANNEL_SUBJECT = BASE_SUBJECT + "channel.";
    public static final String CHANNEL_UPDATE_SUBJECT = CHANNEL_SUBJECT + "update";

    public static final String DATA_PROVIDER_SUBJECT = BASE_SUBJECT + "dataProvider.";
    public static final String DATA_PROVIDER_UPDATE_SUBJECT = DATA_PROVIDER_SUBJECT + "update";

    public static String createHandlerSuccessSubject(String taskName) {
        return String.format("%s%s.success", BASE_SUBJECT, convertTaskName(taskName));
    }

    public static String createHandlerFailureSubject(String taskName, TaskFailureCause cause) {
        return String.format("%s%s.failed.%s", BASE_SUBJECT, convertTaskName(taskName), cause);
    }

    private static String convertTaskName(String taskName) {
        return taskName.toLowerCase().replace(' ', '-');
    }
}
