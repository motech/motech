package org.motechproject.tasks.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.tasks.json.TaskConfigDeserializer;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.collections.CollectionUtils.filter;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@JsonDeserialize(using = TaskConfigDeserializer.class)
public class TaskConfig implements Serializable {
    private static final long serialVersionUID = -3796700837710354216L;

    private SortedSet<TaskConfigStep> steps = new TreeSet<>();

    public SortedSet<TaskConfigStep> getSteps() {
        return steps;
    }

    @JsonIgnore
    public SortedSet<FilterSet> getFilters() {
        SortedSet<FilterSet> set = new TreeSet<>();

        for (TaskConfigStep step : steps) {
            if (step instanceof FilterSet) {
                set.add((FilterSet) step);
            }
        }

        return set;
    }

    @JsonIgnore
    public SortedSet<DataSource> getDataSources() {
        SortedSet<DataSource> set = new TreeSet<>();

        for (TaskConfigStep step : steps) {
            if (step instanceof DataSource) {
                set.add((DataSource) step);
            }
        }

        return set;
    }

    @JsonIgnore
    public SortedSet<DataSource> getDataSources(String providerId) {
        SortedSet<DataSource> set = new TreeSet<>();

        for (TaskConfigStep step : steps) {
            if (step instanceof DataSource) {
                DataSource source = (DataSource) step;

                if (source.getProviderId().equalsIgnoreCase(providerId)) {
                    set.add(source);
                }
            }
        }

        return set;
    }

    @JsonIgnore
    public DataSource getDataSource(final String providerId, final Long objectId,
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
        steps.clear();
        return this;
    }

    public TaskConfig removeFilterSets() {
        filter(steps, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return !(object instanceof FilterSet);
            }
        });

        addAll(new TreeSet<>(steps));

        return this;
    }

    public TaskConfig removeDataSources() {
        filter(steps, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return !(object instanceof DataSource);
            }
        });

        addAll(new TreeSet<>(steps));

        return this;
    }

    public TaskConfig add(TaskConfigStep... configSteps) {
        for (TaskConfigStep step : configSteps) {
            step.setOrder(getNextOrderNumber());

            steps.add(step);
        }

        return this;
    }

    public TaskConfig addAll(SortedSet<TaskConfigStep> set) {
        if (isNotEmpty(set)) {
            for (TaskConfigStep step : set) {
                if (!steps.contains(step)) {
                    add(step);
                }
            }
        }

        return this;
    }

    private Integer getNextOrderNumber() {
        Integer order;

        try {
            order = steps.last().getOrder() + 1;
        } catch (NoSuchElementException e) {
            order = 0;
        }

        return order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps);
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

        return Objects.equals(this.steps, other.steps);
    }

    @Override
    public String toString() {
        return String.format("TaskConfig{steps=%s}", steps);
    }

}
