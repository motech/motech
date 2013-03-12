package org.motechproject.tasks.validation;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class ValidationResult {
    private List<TaskError> taskErrors = new ArrayList<>();

    public void addErrors(ValidationResult result) {
        if (!result.isValid()) {
            taskErrors.addAll(result.getTaskErrors());
        }
    }

    public void addError(TaskError taskError) {
        if (taskError != null) {
            taskErrors.add(taskError);
        }
    }

    public List<TaskError> getTaskErrors() {
        return taskErrors;
    }

    public boolean isValid() {
        return isEmpty(taskErrors);
    }
}
