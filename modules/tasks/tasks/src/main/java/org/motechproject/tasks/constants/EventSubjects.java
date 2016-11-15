package org.motechproject.tasks.constants;

/**
 * Utility class containing various event subject values used throughout the tasks module
 */
public final class EventSubjects {

    /**
     * Utility class, should not be instantiated.
     */
    private EventSubjects() {
    }

    public static final String BASE_SUBJECT = "org.motechproject.tasks.";

    public static final String CHANNEL_SUBJECT = BASE_SUBJECT + "channel.";
    public static final String CHANNEL_UPDATE_SUBJECT = CHANNEL_SUBJECT + "update";
    public static final String CHANNEL_REGISTER_SUBJECT = CHANNEL_SUBJECT + "register";
    public static final String CHANNEL_DEREGISTER_SUBJECT = CHANNEL_SUBJECT + "deregister";

    public static final String DATA_PROVIDER_SUBJECT = BASE_SUBJECT + "dataProvider.";
    public static final String DATA_PROVIDER_UPDATE_SUBJECT = DATA_PROVIDER_SUBJECT + "update";

    public static final String SCHEDULE_REPEATING_JOB = "scheduleRepeatingJob";

    /**
     * Creates a subject used by the task trigger handler to send an event notifying about successful task execution.
     *
     * @param taskName  the name of the task, not null
     * @return  the subject
     */
    public static String createHandlerSuccessSubject(String taskName) {
        return String.format("%s%s.success", BASE_SUBJECT, convertTaskName(taskName));
    }

    /**
     * Creates a subject used by the task trigger handler to send an event notifying about a failure during task
     * execution.
     *
     * @param taskName  the name of the task, not null
     * @param cause  the cause of the failure, not null
     * @return  the subject
     */
    public static String createHandlerFailureSubject(String taskName, TaskFailureCause cause) {
        return String.format("%s%s.failed.%s", BASE_SUBJECT, convertTaskName(taskName), cause);
    }

    private static String convertTaskName(String taskName) {
        return taskName.toLowerCase().replace(' ', '-');
    }
}
