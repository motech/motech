package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Arrays;
import java.util.Objects;

@TypeDiscriminator("doc.type == 'TaskActivity'")
public class TaskActivity extends MotechBaseDataObject {
    private static final long serialVersionUID = 4700697701096557098L;

    private String message;
    private String task;
    private String[] fields;
    private DateTime date;
    private TaskActivityType activityType;
    private String stackTraceElement;

    public TaskActivity() {
        this(null, null, null);
    }

    public TaskActivity(String message, String task, TaskActivityType activityType) {
        this(message, (String[]) null, task, activityType);
    }

    public TaskActivity(String message, String field, String task, TaskActivityType activityType) {
        this(message, new String[]{field}, task, activityType);
    }

    public TaskActivity(String message, String[] fields, String task, TaskActivityType activityType) {
        this(message, fields, task, activityType, null);
    }

    public TaskActivity(String message, String[] fields, String task, TaskActivityType activityType, String stackTraceElement) {
        this.message = message;
        this.fields = fields != null ? Arrays.copyOf(fields, fields.length) : new String[0];
        this.task = task;
        this.date = DateTimeSourceUtil.now();
        this.activityType = activityType;
        this.stackTraceElement = stackTraceElement;
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

    public String[] getFields() {
        return Arrays.copyOf(fields, fields.length);
    }

    public void setFields(String[] fields) {
        this.fields = fields != null ? Arrays.copyOf(fields, fields.length) : new String[0];
    }

    public void setField(String field) {
        this.fields = field != null ? new String[]{field} : new String[0];
    }

    public DateTime getDate() {
        return DateUtil.setTimeZone(date);
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

    public String getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(String stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskActivity other = (TaskActivity) obj;

        return Objects.equals(this.message, other.message) &&
                Objects.equals(this.task, other.task) &&
                Arrays.equals(this.fields, other.fields) &&
                Objects.equals(getDate(), other.getDate()) &&
                Objects.equals(this.activityType, other.activityType) &&
                Objects.equals(this.stackTraceElement, other.stackTraceElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, task, fields, getDate(), activityType, stackTraceElement);
    }

    @Override
    public String toString() {
        return String.format("TaskActivity{message='%s', task='%s', field='%s', date=%s, activityType=%s, stackTrace=%s}",
                message, task, Arrays.toString(fields), date, activityType, stackTraceElement);
    }
}
