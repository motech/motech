package org.motechproject.tasks.dto;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataSourceDto.class),
        @JsonSubTypes.Type(value = FilterSetDto.class)
})
public abstract class TaskConfigStepDto implements Comparable<TaskConfigStepDto> {

    private Integer order;

    protected TaskConfigStepDto(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int compareTo(TaskConfigStepDto o) {
        return Integer.compare(this.order, o.order);
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

        final TaskConfigStepDto other = (TaskConfigStepDto) obj;

        return Objects.equals(this.order, other.order);
    }
}
