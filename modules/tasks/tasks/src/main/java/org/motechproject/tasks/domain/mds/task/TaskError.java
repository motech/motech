package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.enums.TaskErrorType;
import org.motechproject.tasks.dto.TaskErrorDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a single task error. Those error are encountered during validation of a channel if some of the required
 * fields are blank or missing.
 */
@Entity(recordHistory = true, nonEditable = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class TaskError implements Serializable {

    private static final long serialVersionUID = -602791178447970480L;

    @Field
    private List<String> args;

    @Field(required = true)
    private String message;

    /**
     * Constructor.
     */
    public TaskError() {
        this((String) null);
    }

    /**
     * Constructor.
     *
     * @param type  the error type, not null
     * @param args  the arguments
     */
    public TaskError(TaskErrorType type, String... args) {
        this(type.getMessage(), args);
    }

    /**
     * Constructor.
     *
     * @param message  the error message
     * @param args  the arguments
     */
    public TaskError(String message, String... args) {
        this.args = args == null ? new ArrayList<String>() : Arrays.asList(args);
        this.message = message;
    }

    public TaskErrorDto toDto() {
        return new TaskErrorDto(message, args);
    }

    public static Set<TaskErrorDto> toDtos(Set<TaskError> errors) {
        Set<TaskErrorDto> errorDtos = new HashSet<>();

        for (TaskError error : errors) {
            errorDtos.add(error.toDto());
        }

        return errorDtos;
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
