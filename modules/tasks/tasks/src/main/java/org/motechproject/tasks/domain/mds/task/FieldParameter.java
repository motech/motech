package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.mds.Parameter;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.dto.FieldParameterDto;

import java.util.Objects;

/**
 * Represents a single field of the {@link TaskDataProviderObject} that is part of the {@link TaskDataProvider}.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class FieldParameter extends Parameter {

    private static final long serialVersionUID = -2789552939112269521L;

    private String fieldKey;

    /**
     * Constructor.
     */
    public FieldParameter() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the parameter display name
     * @param fieldKey  the field key
     */
    public FieldParameter(String displayName, String fieldKey) {
        this(displayName, fieldKey, ParameterType.UNICODE);
    }

    /**
     * Constructor.
     *
     * @param displayName  the parameter display name
     * @param fieldKey  the field key
     * @param type  the parameter type
     */
    public FieldParameter(final String displayName, final String fieldKey, final ParameterType type) {
        super(displayName, type);
        this.fieldKey = fieldKey;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(final String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public FieldParameterDto toDto() {
        return new FieldParameterDto(getDisplayName(), getType(), fieldKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final FieldParameter other = (FieldParameter) obj;

        return Objects.equals(this.fieldKey, other.fieldKey);
    }

    @Override
    public String toString() {
        return String.format("FieldParameter{fieldKey='%s'} %s", fieldKey, super.toString());
    }
}
