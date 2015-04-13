package org.motechproject.tasks.domain;

import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;

import java.util.Objects;

import static org.motechproject.tasks.domain.ParameterType.UNICODE;

/**
 * Object representation of a parameter in the channel action definition.
 *
 * @see ActionEvent
 * @since 0.19
 */
@Entity
@CrudEvents(CrudEventType.NONE)
public class ActionParameter extends Parameter implements Comparable<ActionParameter> {
    private static final long serialVersionUID = 8204529887802399508L;

    @Field
    private Integer order;
    @Field
    private String key;
    @Field
    private String value;
    @Field
    private Boolean required;
    @Field
    private Boolean hidden;

    public ActionParameter() {
        this(null, UNICODE, null, null, null, false, false);
    }

    public ActionParameter(String displayName, ParameterType type, Integer order, String key, String value,
                           Boolean required, Boolean hidden) {
        super(displayName, type);
        this.order = order;
        this.key = key;
        this.value = value;
        this.required = required;
        this.hidden = hidden;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isRequired() {
        return required != null && required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public boolean isHidden() {
        return hidden != null && hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public int compareTo(ActionParameter o) {
        return Integer.compare(this.order, o.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key, value, required, hidden);
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

        final ActionParameter other = (ActionParameter) obj;
        return Objects.equals(this.order, other.order)
                && Objects.equals(this.key, other.key)
                && Objects.equals(this.value, other.value)
                && Objects.equals(this.required, other.required)
                && Objects.equals(this.hidden, other.hidden);
    }

    @Override
    public String toString() {
        return "ActionParameter{" +
                "order=" + order +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", required=" + required +
                ", hidden=" + hidden +
                '}';
    }
}
