package org.motechproject.tasks.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public class SettingsDto {

    private String taskPossibleErrors;

    public String getTaskPossibleErrors() {
        return taskPossibleErrors;
    }

    public void setTaskPossibleErrors(String taskPossibleErrors) {
        this.taskPossibleErrors = taskPossibleErrors;
    }

    @JsonIgnore
    public boolean isValid() {
        if (StringUtils.isEmpty(taskPossibleErrors) || !StringUtils.isNumeric(taskPossibleErrors)) {
            return false;
        }
        return true;
    }

}
