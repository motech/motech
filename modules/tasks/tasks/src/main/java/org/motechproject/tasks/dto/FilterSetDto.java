package org.motechproject.tasks.dto;

import org.motechproject.tasks.domain.enums.LogicalOperator;

import java.util.List;
import java.util.Objects;

public class FilterSetDto extends TaskConfigStepDto {

    private List<FilterDto> filters;
    private LogicalOperator operator;

    public FilterSetDto() {
        super(null);
    }

    public FilterSetDto(Integer order, List<FilterDto> filters, LogicalOperator operator) {
        super(order);
        this.filters = filters;
        this.operator = operator;
    }

    public List<FilterDto> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDto> filters) {
        this.filters = filters;
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

        final FilterSetDto other = (FilterSetDto) obj;

        return Objects.equals(this.filters, other.filters) &&
                Objects.equals(this.operator, other.operator);
    }
}
