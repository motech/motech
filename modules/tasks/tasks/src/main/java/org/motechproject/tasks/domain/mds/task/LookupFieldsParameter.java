package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.LookupFieldsParameterDto;

import java.util.List;
import java.util.Objects;

/**
 * Represents a lookup fields. It is part of the {@link TaskDataProviderObject} and describes a single lookup with its
 * display name and fields that are used by that lookup.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class LookupFieldsParameter {

    private String displayName;
    private List<String> fields;

    public LookupFieldsParameter() {
        this(null, null);
    }

    public LookupFieldsParameter(String displayName, List<String> fields) {
        this.displayName = displayName;
        this.fields = fields;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LookupFieldsParameterDto toDto() {
        return new LookupFieldsParameterDto(displayName, fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, fields);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final LookupFieldsParameter other = (LookupFieldsParameter) obj;

        return Objects.equals(this.displayName, other.getDisplayName())
                && Objects.equals(this.fields, other.getFields());
    }

    @Override
    public String toString() {
        return String.format("LookupFieldsParameter{displayName='%s', fields=%s}", displayName, fields);
    }
}
