package org.motechproject.tasks.dto;

import org.motechproject.tasks.domain.enums.ParameterType;

import java.util.Objects;
import java.util.SortedSet;

public class ActionParameterDto extends ParameterDto implements Comparable<ActionParameterDto> {

    private Integer order;
    private String key;
    private String value;
    private Boolean required;
    private Boolean hidden;
    private SortedSet<String> options;

    public ActionParameterDto(String displayName, ParameterType type, Integer order, String key, String value, Boolean required, Boolean hidden, SortedSet<String> options) {
        super(displayName, type);
        this.order = order;
        this.key = key;
        this.value = value;
        this.required = required;
        this.hidden = hidden;
        this.options = options;
    }

    @Override
    public int compareTo(ActionParameterDto o) {
        return Integer.compare(this.order, o.order);
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public SortedSet<String> getOptions() {
        return options;
    }

    public void setOptions(SortedSet<String> options) {
        this.options = options;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key, value, required, hidden, options);
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

        final ActionParameterDto other = (ActionParameterDto) obj;
        return Objects.equals(this.order, other.order)
                && Objects.equals(this.key, other.key)
                && Objects.equals(this.value, other.value)
                && Objects.equals(this.required, other.required)
                && Objects.equals(this.hidden, other.hidden)
                && Objects.equals(this.options, other.options);
    }
}
