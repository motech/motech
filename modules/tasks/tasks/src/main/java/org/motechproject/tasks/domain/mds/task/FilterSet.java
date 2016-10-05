package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.enums.LogicalOperator;
import org.motechproject.tasks.dto.FilterDto;
import org.motechproject.tasks.dto.FilterSetDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a set of {@link Filter}s. Those are conditions that task must meet before being executed. If the
 * conditions are not met the task execution will be stopped. Joining those conditions can be done by using logical "and"
 * or logical "or" as an operator and can be set by {@link #setOperator(LogicalOperator)} method.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class FilterSet extends TaskConfigStep {
    private static final long serialVersionUID = 6046402871816204829L;

    @Field
    @Cascade(delete = true)
    private List<Filter> filters;

    @Field
    private LogicalOperator operator;

    /**
     * Constructor.
     */
    public FilterSet() {
        this(new ArrayList<>());
    }

    /**
     * Constructor.
     * @param dto FilterSet data transfer object
     */
    public FilterSet(FilterSetDto dto) {
        this(Filter.toFilters(dto.getFilters()), dto.getOperator(), dto.getOrder());
    }

    /**
     * Constructor. The operator will be set to "AND".
     *
     * @param filters  the filters
     */
    public FilterSet(List<Filter> filters) {
        this(filters, LogicalOperator.AND);
    }

    /**
     * Constructor.
     *
     * @param filters  the filters
     * @param operator  the operator, can be "AND" or "OR
     */
    public FilterSet(List<Filter> filters, LogicalOperator operator) {
        this(filters, operator, null);
    }

    public FilterSet(List<Filter> filters, LogicalOperator operator, Integer order) {
        super(order);
        this.filters = filters == null ? new ArrayList<>() : filters;
        this.operator = operator;
    }

    public void addFilter(Filter filter) {
        if (filter != null) {
            filters.add(filter);
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters.clear();

        if (filters != null) {
            this.filters = filters;
        }
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }

    public FilterSetDto toDto() {
        List<FilterDto> filterDtos = new ArrayList<>();

        for (Filter filter : filters) {
            filterDtos.add(filter.toDto());
        }

        return new FilterSetDto(getOrder(), filterDtos, operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters, operator);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final FilterSet other = (FilterSet) obj;

        return Objects.equals(this.filters, other.filters) &&
                Objects.equals(this.operator, other.operator);
    }

    @Override
    public String toString() {
        return String.format("FilterSet{filters=%s, operator=%s} %s", filters, operator, super.toString());
    }
}
