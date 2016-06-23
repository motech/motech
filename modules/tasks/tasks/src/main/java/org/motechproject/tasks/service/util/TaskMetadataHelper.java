package org.motechproject.tasks.service.util;

import java.util.Map;

import static org.motechproject.tasks.constants.EventDataKeys.TASK_RETRY;

public final class TaskMetadataHelper {

    public static boolean isRetryScheduled(Map<String, Object> metadata) {
        return metadata.get(TASK_RETRY) != null;
    }
}
