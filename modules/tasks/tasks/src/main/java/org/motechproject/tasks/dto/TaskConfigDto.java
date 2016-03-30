package org.motechproject.tasks.dto;

import java.util.SortedSet;

public class TaskConfigDto {

    private SortedSet<TaskConfigStepDto> steps;

    public TaskConfigDto(SortedSet<TaskConfigStepDto> steps) {
        this.steps = steps;
    }

    public SortedSet<TaskConfigStepDto> getSteps() {
        return steps;
    }

    public void setSteps(SortedSet<TaskConfigStepDto> steps) {
        this.steps = steps;
    }
}
