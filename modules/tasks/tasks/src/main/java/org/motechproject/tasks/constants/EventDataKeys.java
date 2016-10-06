package org.motechproject.tasks.constants;

/**
 * Utility class storing keys for event data.
 */
public final class EventDataKeys {

    /**
     * Utility class, should not be instantiated.
     */
    private EventDataKeys() {
    }

    public static final String TASK_FAIL_MESSAGE = "taskFailMessage";
    public static final String TASK_FAIL_STACK_TRACE = "taskFailStackTrace";
    public static final String TASK_FAIL_FAILURE_DATE = "taskFailFailureDate";
    public static final String TASK_FAIL_FAILURE_NUMBER = "taskFailFailureNumber";
    public static final String TASK_FAIL_TRIGGER_DISABLED = "taskFailTriggerDisabled";
    public static final String TASK_FAIL_TASK_ID = "taskFailTaskID";
    public static final String TASK_FAIL_TASK_NAME = "taskFailTaskName";

    public static final String CHANNEL_MODULE_NAME = "moduleName";

    public static final String DATA_PROVIDER_NAME = "name";

    public static final String HANDLER_ERROR_PARAM = "taskError";

    public static final String REPEAT_COUNT = "repeatCount";
    public static final String REPEAT_INTERVAL_TIME = "repeatIntervalInSeconds";
    public static final String JOB_SUBJECT = "jobSubject";
    public static final String TASK_ID = "task_ID";
    public static final String TASK_ACTIVITY_ID = "task_activity_ID";
    public static final String TASK_RETRY = "taskRetry";

}
