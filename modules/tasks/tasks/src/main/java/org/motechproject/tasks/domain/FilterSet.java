package org.motechproject.tasks.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilterSet extends TaskConfigStep {
    private static final long serialVersionUID = 6046402871816204829L;

    private List<Filter> filters;

    public FilterSet() {
        this(null);
    }

    public FilterSet(List<Filter> filters) {
        this.filters = filters == null ? new ArrayList<Filter>() : filters;
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

    @Override
    public int hashCode() {
        return Objects.hash(filters);
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

        return Objects.equals(this.filters, other.filters);
    }

    @Override
    public String toString() {
        return String.format("FilterSet{filters=%s} %s", filters, super.toString());
    }
}
