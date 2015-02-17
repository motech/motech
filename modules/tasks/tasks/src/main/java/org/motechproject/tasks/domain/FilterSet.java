package org.motechproject.tasks.domain;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(recordHistory = true)
public class FilterSet extends TaskConfigStep {
    private static final long serialVersionUID = 6046402871816204829L;

    @Field
    @Cascade(delete = true)
    private List<Filter> filters;

    @Field
    private LogicalOperator operator;

    public FilterSet() {
        this(null);
    }

    public FilterSet(List<Filter> filters) {
        this(filters, LogicalOperator.AND);
    }

    public FilterSet(List<Filter> filters, LogicalOperator operator) {
        this.filters = filters == null ? new ArrayList<Filter>() : filters;
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
