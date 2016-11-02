package org.motechproject.tasks.domain.mds.channel;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.mds.Parameter;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.dto.ActionParameterDto;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.motechproject.tasks.domain.enums.ParameterType.UNICODE;

/**
 * Represents a single parameter of an action in the channel definition.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class ActionParameter extends Parameter implements Comparable<ActionParameter> {
    private static final long serialVersionUID = 8204529887802399508L;

    @Field
    private Integer order;

    @Field(required = true)
    private String key;

    @Field
    private String value;

    @Field
    private Boolean required;

    @Field
    private Boolean hidden;

    @Field
    @Cascade(delete = true)
    private SortedSet<String> options;

    /**
     * Constructor.
     */
    public ActionParameter() {
        this(null, UNICODE, null, null, null, false, false, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the parameter display name
     * @param type  the parameter type
     * @param order  the parameter order
     * @param key  the parameter key
     * @param value  the parameter value
     * @param required  defines whether the parameter is required
     * @param hidden  defines whether the parameter is hidden
     * @param options the parameter options for select parameter type
     */
    public ActionParameter(String displayName, ParameterType type, Integer order, String key, String value,
                           Boolean required, Boolean hidden, SortedSet<String> options) {
        super(displayName, type);
        this.order = order;
        this.key = key;
        this.value = value;
        this.required = required;
        this.hidden = hidden;
        this.options = options == null ? new TreeSet<>() : options;
    }

    public ActionParameter(ActionParameter actionParameter) {
        this(actionParameter.getDisplayName(), actionParameter.getType(), actionParameter.getOrder(),
                actionParameter.getKey(), actionParameter.getValue(), actionParameter.isRequired(),
                actionParameter.isHidden(), actionParameter.getOptions());
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

    public SortedSet<String> getOptions() {
        return options;
    }

    public void setOptions(SortedSet<String> options) {
        this.options = options;
    }

    public ActionParameterDto toDto() {
        return new ActionParameterDto(getDisplayName(), getType(), order, key, value, required, hidden, options);
    }

    @Override
    public int compareTo(ActionParameter o) {
        return Integer.compare(this.order, o.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key, value, required, hidden, options);
    }

    //CHECKSTYLE:OFF
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
                && Objects.equals(this.hidden, other.hidden)
                && Objects.equals(this.options, other.options);
    }

    //CHECKSTYLE:ON
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
