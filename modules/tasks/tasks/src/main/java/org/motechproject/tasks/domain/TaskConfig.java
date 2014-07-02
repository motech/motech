package org.motechproject.tasks.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.tasks.json.TaskConfigDeserializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Entity
@JsonDeserialize(using = TaskConfigDeserializer.class)
public class TaskConfig implements Serializable {
    private static final long serialVersionUID = -3796700837710354216L;

    @Field
    @Cascade(delete = true)
    private List<FilterSet> filters;

    @Field
    @Cascade(delete = true)
    private List<DataSource> dataSources;

    @Ignore
    public SortedSet<TaskConfigStep> getSteps() {
        SortedSet<TaskConfigStep> steps = new TreeSet<>();

        steps.addAll(getFilters());
        steps.addAll(getDataSources());

        return steps;
    }

    @JsonIgnore
    public List<FilterSet> getFilters() {
        if (filters == null) {
            filters = new ArrayList<>();
        }
        return filters;
    }

    @JsonIgnore
    public void setFilters(List<FilterSet> filters) {
        this.filters = filters;
    }

    @JsonIgnore
    public List<DataSource> getDataSources() {
        if (dataSources == null) {
            dataSources = new ArrayList<>();
        }
        return dataSources;
    }

    @JsonIgnore
    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @JsonIgnore
    public SortedSet<DataSource> getDataSources(Long providerId) {
        SortedSet<DataSource> set = new TreeSet<>();

        for (DataSource source : getDataSources()) {
            if (source.getProviderId().equals(providerId)) {
                set.add(source);
            }
        }

        return set;
    }

    @JsonIgnore
    public DataSource getDataSource(final Long providerId, final Long objectId,
                                    final String objectType) {
        return (DataSource) CollectionUtils.find(getDataSources(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof DataSource
                        && ((DataSource) object).objectEquals(providerId, objectId, objectType);
            }
        });
    }

    public TaskConfig removeAll() {
        removeFilterSets();
        removeDataSources();

        return this;
    }

    public TaskConfig removeFilterSets() {
        getFilters().clear();
        return this;
    }

    public TaskConfig removeDataSources() {
        getDataSources().clear();
        return this;
    }

    public TaskConfig add(TaskConfigStep... configSteps) {
        for (TaskConfigStep step : configSteps) {
            step.setOrder(getNextOrderNumber());

            if (step instanceof FilterSet) {
                getFilters().add((FilterSet) step);
            } else if (step instanceof DataSource) {
                getDataSources().add((DataSource) step);
            }
        }

        return this;
    }

    public TaskConfig addAll(SortedSet<TaskConfigStep> set) {
        if (isNotEmpty(set)) {
            SortedSet<TaskConfigStep> steps = getSteps();

            for (TaskConfigStep step : set) {
                if (!steps.contains(step)) {
                    add(step);
                }
            }
        }

        return this;
    }

    @Ignore
    private Integer getNextOrderNumber() {
        Integer order;

        try {
            order = getSteps().last().getOrder() + 1;
        } catch (NoSuchElementException e) {
            order = 0;
        }

        return order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilters(), getDataSources());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskConfig other = (TaskConfig) obj;

        return Objects.equals(this.getFilters(), other.getFilters())
                && Objects.equals(this.getDataSources(), other.getDataSources());
    }

    @Override
    public String toString() {
        return String.format("TaskConfig{steps=%s}", getSteps());
    }

}
