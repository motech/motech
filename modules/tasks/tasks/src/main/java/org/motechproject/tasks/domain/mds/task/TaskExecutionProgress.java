package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.Objects;

/**
 * A domain object that keeps track of the task execution progress.
 */
@Entity
public class TaskExecutionProgress {

    @Field
    private int totalActions;

    @Field
    private int actionsSucceeded;

    public TaskExecutionProgress(int totalActions) {
        this.totalActions = totalActions;
    }

    public int getTotalActions() {
        return totalActions;
    }

    public void setTotalActions(int totalActions) {
        this.totalActions = totalActions;
    }

    public int getActionsSucceeded() {
        return actionsSucceeded;
    }

    public void addSuccess() {
        actionsSucceeded++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskExecutionProgress)) {
            return false;
        }
        TaskExecutionProgress that = (TaskExecutionProgress) o;
        return totalActions == that.totalActions &&
                actionsSucceeded == that.actionsSucceeded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalActions, actionsSucceeded);
    }
}
