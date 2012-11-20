package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'TaskStatusMessage'")
public class TaskStatusMessage extends MotechBaseDataObject {
    private String message;
    private String task;
    private DateTime date;
    private Level level;

    public TaskStatusMessage() {
        this(null, null, null);
    }

    public TaskStatusMessage(String message, String task, Level level) {
        this.message = message;
        this.task = task;
        this.date = DateTime.now();
        this.level = level;
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskStatusMessage that = (TaskStatusMessage) o;

        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }

        if (level != that.level) {
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
        result = 31 * result + (level != null ? level.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("TaskStatusMessage{message='%s', task='%s', date=%s, level=%s}",
                message, task, date, level);
    }
}
