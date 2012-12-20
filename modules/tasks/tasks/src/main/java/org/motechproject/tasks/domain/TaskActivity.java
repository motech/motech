package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'TaskActivity'")
public class TaskActivity extends MotechBaseDataObject {
    private String message;
    private String task;
    private DateTime date;
    private TaskActivityType activityType;

    public TaskActivity() {
        this(null, null, null);
    }

    public TaskActivity(String message, String task, TaskActivityType activityType) {
        this.message = message;
        this.task = task;
        this.date = DateTime.now();
        this.activityType = activityType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getTask() {
        return task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(final DateTime date) {
        this.date = date;
    }

    public TaskActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(final TaskActivityType activityType) {
        this.activityType = activityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskActivity that = (TaskActivity) o;

        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }

        if (activityType != that.activityType) {
            return false;
        }

        if (message != null ? !message.equals(that.message) : that.message != null) {
            return false;
        }

        if (task != null ? !task.equals(that.task) : that.task != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (task != null ? task.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (activityType != null ? activityType.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("TaskActivity{message='%s', task='%s', date=%s, activityType=%s}",
                message, task, date, activityType);
    }
}
