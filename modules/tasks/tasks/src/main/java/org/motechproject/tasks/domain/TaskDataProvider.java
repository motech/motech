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
}
