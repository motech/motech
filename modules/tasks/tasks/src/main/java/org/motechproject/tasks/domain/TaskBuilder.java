package org.motechproject.tasks.domain;

import java.util.ArrayList;
import java.util.List;

public class TaskBuilder {
    private String description;
    private String name;
    private List<TaskActionInformation> actions;
    private TaskEventInformation trigger;
    private boolean enabled;
    private TaskConfig taskConfig;

    public TaskBuilder() {
        taskConfig = new TaskConfig();
        actions = new ArrayList<TaskActionInformation>();
        enabled = false;
    }

    public TaskBuilder withName(String name) {
        this.name = name;

        return this;
    }

    public TaskBuilder withDescription(String description) {
        this.description = description;

        return this;
    }

    public TaskBuilder withTrigger(TaskEventInformation trigger) {
        this.trigger = trigger;

        return this;
    }

    public TaskBuilder withTaskConfig(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;

        return this;
    }

    public TaskBuilder addAction(TaskActionInformation action) {
        this.actions.add(action);

        return this;
    }

    public TaskBuilder addFilterSet(FilterSet filterSet) {
        this.taskConfig.add(filterSet);

        return this;
    }

    public TaskBuilder addDataSource(DataSource dataSource) {
        this.taskConfig.add(dataSource);

        return this;
    }

    public TaskBuilder isEnabled(boolean enabled) {
        this.enabled = enabled;

        return this;
    }

    public TaskBuilder clear() {
        name = "";
        description = "";
        actions.clear();
        trigger = null;
        enabled = false;
        taskConfig.removeAll();

        return this;
    }

    public Task build() {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setTrigger(trigger);
        task.setEnabled(enabled);
        task.setActions(actions);
        task.setTaskConfig(taskConfig);

        return task;
    }
}
