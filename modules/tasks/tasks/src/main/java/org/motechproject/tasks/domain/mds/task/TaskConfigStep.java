package org.motechproject.tasks.domain.mds.task;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.tasks.dto.TaskConfigStepDto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a single task configuration step. Task configuration step is an abstract class that should be extended by
 * all steps that are taken during task execution. Currently it serves as a base class for {@link Filter}s and
 * {@link DataSource}s.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataSource.class),
        @JsonSubTypes.Type(value = FilterSet.class)
})
public abstract class TaskConfigStep implements Comparable<TaskConfigStep>, Serializable {

    private static final long serialVersionUID = -6415130097686935451L;

    private Integer order;

    public TaskConfigStep() {
    }

    public TaskConfigStep(Integer order) {
        this.order = order;
    }

    public abstract TaskConfigStepDto toDto();

    @Override
    public int compareTo(TaskConfigStep o) {
        return Integer.compare(this.order, o.order);
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskConfigStep other = (TaskConfigStep) obj;

        return Objects.equals(this.order, other.order);
    }

    @Override
    public String toString() {
        return String.format("TaskConfigStep{order=%d}", order);
    }
}
