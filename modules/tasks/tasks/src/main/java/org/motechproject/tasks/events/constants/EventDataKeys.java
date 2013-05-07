package org.motechproject.tasks.events.constants;

public final class EventDataKeys {
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
}
