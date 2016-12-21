package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.dto.FilterDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single filter. A filter is a part of the {@link FilterSet} and represents a single condition that task
 * must meet before being executed. If that condition is not met the task execution will be stopped. It is an optional
 * part of a task.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class Filter implements Serializable {

    private static final long serialVersionUID = 7811400954352375064L;

    @Field(required = true)
    private String displayName;

    @Field(required = true)
    private String key;

    @Field(required = true)
    private ParameterType type;

    @Field(required = true)
    private String operator;

    @Field
    private boolean negationOperator;

    @Field
    private String expression;

    @Field
    private List<String> manipulations;

    /**
     * Constructor.
     */
    public Filter() {
        this(null, null, null, false, null, null, null);
    }

    /**
     * Constructor.
     * @param dto Filter data transfer object
     */
    public Filter(FilterDto dto){
        this (dto.getDisplayName(), dto.getKey(), dto.getType(), dto.isNegationOperator(), dto.getOperator(),
                dto.getExpression(), dto.getManipulations());
    }

    /**
     * Constructor.
     *
     * @param displayName  the filter display name
     * @param key  the filter key
     * @param type  the filter type
     * @param negationOperator  defines if the represented operator should be negated
     * @param operator  the filter operator
     * @param expression  the filter exception
     */
    public Filter(String displayName, String key, ParameterType type, boolean negationOperator,
                  String operator, String expression, List<String> manipulations) {
        this.displayName = displayName;
        this.key = key;
        this.type = type;
        this.negationOperator = negationOperator;
        this.operator = operator;
        this.expression = expression;
        this.manipulations = manipulations;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(final ParameterType type) {
        this.type = type;
    }

    public boolean isNegationOperator() {
        return negationOperator;
    }

    public void setNegationOperator(boolean negationOperator) {
        this.negationOperator = negationOperator;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<String> getManipulations () {
        return manipulations;
    }

    public void setManipulations (List<String> manipulations) {
        this.manipulations = manipulations;
    }

    public FilterDto toDto() {
        return new FilterDto(displayName, key, type, operator, negationOperator, expression, manipulations);
    }

    public static List<Filter> toFilters(List<FilterDto> filterDtos) {
        List<Filter> filters = new ArrayList<>();

        for (FilterDto filterDto : filterDtos) {
            filters.add(new Filter(filterDto));
        }

        return filters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, key, type, negationOperator, operator, expression, manipulations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Filter other = (Filter) obj;

        return Objects.equals(this.displayName, other.displayName) &&
               Objects.equals(this.key, other.key) &&
               Objects.equals(this.type, other.type) &&
               Objects.equals(this.negationOperator, other.negationOperator) &&
               Objects.equals(this.operator, other.operator) &&
               Objects.equals(this.expression, other.expression) &&
               Objects.equals(this.manipulations, other.manipulations);
    }

    @Override
    public String toString() {
        return String.format(
                "Filter{displayName=%s, key=%s, type=%s, negationOperator=%s, operator='%s', expression='%s', manipulations='%s'}",
                displayName, key, type, negationOperator, operator, expression, manipulations
        );
    }
}
