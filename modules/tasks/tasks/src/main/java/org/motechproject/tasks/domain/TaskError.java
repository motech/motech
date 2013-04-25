package org.motechproject.tasks.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TaskError implements Serializable {
    private static final long serialVersionUID = -602791178447970480L;

    private List<String> args;
    private String message;

    public TaskError() {
        this((String) null);
    }

    public TaskError(TaskErrorType type, String... args) {
        this(type.getMessage(), args);
    }

    public TaskError(String message, String... args) {
        this.args = args == null ? new ArrayList<String>() : Arrays.asList(args);
        this.message = message;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(args, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskError other = (TaskError) obj;

        return Objects.equals(this.args, other.args) &&
                Objects.equals(this.message, other.message);
    }

    @Override
    public String toString() {
        return String.format("{message='%s', args='%s'}", message, args);
    }
}
