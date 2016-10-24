package org.motechproject.tasks.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskErrorDto implements Serializable {
    private static final long serialVersionUID = -8736753910091863820L;

    private List<String> args;
    private String message;

    public TaskErrorDto(String message) {
        this(message, new ArrayList<>());
    }

    public TaskErrorDto(String message, List<String> args) {
        this.message = message;
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getMessage() {
        return message;
    }

    public void setArgs(List<String> args) {
        this.args = args;
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

        final TaskErrorDto other = (TaskErrorDto) obj;

        return Objects.equals(this.args, other.args) &&
                Objects.equals(this.message, other.message);
    }
}