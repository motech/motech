package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@TypeDiscriminator("doc.type == 'TaskDataProvider'")
public class TaskDataProvider extends MotechBaseDataObject {
    private static final long serialVersionUID = -5427486904165895928L;
    private String name;
    private List<TaskDataProviderObject> objects;

    public TaskDataProvider() {
        this(null, new ArrayList<TaskDataProviderObject>());
    }

    public TaskDataProvider(String name, List<TaskDataProviderObject> objects) {
        this.name = name;
        this.objects = objects;
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
        this.objects = objects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskDataProvider that = (TaskDataProvider) o;

        return Objects.equals(name, that.name) && Objects.equals(objects, that.objects);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (objects != null ? objects.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("TaskDataProvider{name='%s', objects=%s}", name, objects);
    }
}
