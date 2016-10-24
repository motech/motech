package org.motechproject.tasks.domain.mds;

import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.tasks.domain.mds.task.FieldParameter;
import org.motechproject.tasks.dto.ParameterDto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract class that stores common information about a single parameter. Serves as a base class for
 * {@link FieldParameter}, {@link ActionParameter} and {@link EventParameter} classes. It is a part of the channel
 * model.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
public abstract class Parameter implements Serializable {
    private static final long serialVersionUID = 7685217883414590275L;

    @Field
    private String displayName;

    @Field
    private ParameterType type;

    /**
     * Constructor.
     */
    protected Parameter() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the parameter display name
     * @param type  the parameter type
     */
    protected Parameter(final String displayName, final ParameterType type) {
        this.displayName = displayName;
        this.type = type;
    }

    public abstract ParameterDto toDto();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(final ParameterType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Parameter other = (Parameter) obj;

        return Objects.equals(this.displayName, other.displayName) && Objects.equals(this.type, other.type);
    }

    @Override
    public String toString() {
        return String.format("Parameter{displayName='%s', type=%s}", displayName, type);
    }
}
