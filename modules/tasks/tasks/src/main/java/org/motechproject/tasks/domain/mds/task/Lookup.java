package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.LookupDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single lookup. Lookup is a method of retrieving an object from a data provider. It is a part of a task
 * model.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class Lookup implements Serializable {

    private static final long serialVersionUID = -3560581906854128062L;

    @Field
    private String field;

    @Field
    private String value;

    /**
     * Constructor.
     */
    public Lookup() {
        this(null, null);
    }

    /**
     * Constructor.
     * @param dto Lookup data transfer object
     */
    public Lookup(LookupDto dto){
        this(dto.getField(), dto.getValue());
    }

    /**
     * Constructor.
     *
     * @param field  the field name
     * @param value  the field value
     */
    public Lookup(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LookupDto toDto() {
        return new LookupDto(field, value);
    }

    public static List<Lookup> toLookups(List<LookupDto> lookupDtos) {
        List<Lookup> lookups = new ArrayList<>();

        for (LookupDto lookupDto : lookupDtos) {
            lookups.add(new Lookup(lookupDto));
        }

        return lookups;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Lookup other = (Lookup) obj;

        return Objects.equals(this.field, other.field)
                && Objects.equals(this.value, other.value);
    }
}
