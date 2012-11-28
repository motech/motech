package org.motechproject.tasks.util;

public final class TaskUtil {
    public static final Integer CHANNEL_NAME_IDX = 0;
    public static final Integer MODULE_NAME_IDX = 1;
    public static final Integer MODULE_VERSION_IDX = 2;
    public static final Integer SUBJECT_IDX = 3;

    private TaskUtil() {
    }

    public static String getChannelName(final String taskEvent) {
        return taskEvent.split(":")[CHANNEL_NAME_IDX];
    }

    public static String getModuleName(final String taskEvent) {
        return taskEvent.split(":")[MODULE_NAME_IDX];
    }

    public static String getModuleVersion(final String taskEvent) {
        return taskEvent.split(":")[MODULE_VERSION_IDX];
    }

    public static String getSubject(final String taskEvent) {
        return taskEvent.split(":")[SUBJECT_IDX];
    }

}
