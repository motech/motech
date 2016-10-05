package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.TaskDataProviderDto;
import org.motechproject.tasks.dto.TaskDataProviderObjectDto;
import javax.jdo.annotations.Unique;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

/**
 * Represents a single data provider used by the task module. It provides provider objects used as data sources by the
 * tasks.
 */
@Entity
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class TaskDataProvider {

    @Field
    private Long id;

    @Field(required = true)
    @Unique
    private String name;

    @Field
    @Cascade(delete = true)
    private List<TaskDataProviderObject> objects;

    public TaskDataProvider() {
        this(null, new ArrayList<TaskDataProviderObject>());
    }

    public TaskDataProvider(String name, List<TaskDataProviderObject> objects) {
        this.name = name;
        this.objects = objects == null ? new ArrayList<TaskDataProviderObject>() : objects;
    }

    public boolean containsProviderObject(String type) {
        boolean found = false;

        for (TaskDataProviderObject object : getObjects()) {
            if (equalsIgnoreCase(object.getType(), type)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public boolean containsProviderObjectLookup(String type, String lookupField) {
        TaskDataProviderObject providerObject = getProviderObject(type);

        if (providerObject != null) {
            for (LookupFieldsParameter lookup : providerObject.getLookupFields()) {
                if (lookup.getDisplayName().equals(lookupField)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsProviderObjectField(String type, String fieldKey) {
        TaskDataProviderObject providerObject = getProviderObject(type);

        if (providerObject != null) {
            for (FieldParameter fieldParameter : providerObject.getFields()) {
                if (fieldParameter.getFieldKey().equals(fieldKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    public TaskDataProviderObject getProviderObject(String type) {
        TaskDataProviderObject found = null;

        for (TaskDataProviderObject object : getObjects()) {
            if (equalsIgnoreCase(object.getType(), type)) {
                found = object;
                break;
            }
        }

        return found;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskDataProviderObject> getObjects() {
        return objects;
    }

    public void setObjects(List<TaskDataProviderObject> objects) {
        this.objects.clear();

        if (objects != null) {
            this.objects.addAll(objects);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskDataProviderDto toDto() {
        List<TaskDataProviderObjectDto> objectDtos = new ArrayList<>();

        if (objects != null) {
            for (TaskDataProviderObject object : objects) {
                objectDtos.add(object.toDto());
            }
        }
        return new TaskDataProviderDto(id, name, objectDtos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskDataProvider other = (TaskDataProvider) obj;

        return Objects.equals(this.name, other.name) && Objects.equals(this.objects, other.objects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, objects);
    }

    @Override
    public String toString() {
        return String.format("TaskDataProvider{name='%s', objects=%s}", name, objects);
    }

    public String getKeyType(String key) {
        String type = "UNKNOWN";

        for (TaskDataProviderObject object : getObjects()) {
            for (FieldParameter fieldParameter : object.getFields()) {
                if (equalsIgnoreCase(fieldParameter.getFieldKey(), key)) {
                    type = fieldParameter.getType().toString();
                    break;
                }
            }
        }

        return type;
    }
}
