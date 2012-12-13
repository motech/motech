package org.motechproject.tasks.util;

public final class TaskUtil {
    public static final Integer CHANNEL_NAME_IDX = 0;
    public static final Integer MODULE_NAME_IDX = 1;
    public static final Integer MODULE_VERSION_IDX = 2;
    public static final Integer SUBJECT_IDX = 3;
    public static final String SEPARATOR = ":";

    private TaskUtil() {
    }

    public static String getChannelName(final String taskEvent) {
        return taskEvent.split(SEPARATOR)[CHANNEL_NAME_IDX];
    }

    public static String getModuleName(final String taskEvent) {
        return taskEvent.split(SEPARATOR)[MODULE_NAME_IDX];
    }

    public static String getModuleVersion(final String taskEvent) {
        return taskEvent.split(SEPARATOR)[MODULE_VERSION_IDX];
    }

    public static String getSubject(final String taskEvent) {
        return taskEvent.split(SEPARATOR)[SUBJECT_IDX];
    }

}
