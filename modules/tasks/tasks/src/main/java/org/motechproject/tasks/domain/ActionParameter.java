package org.motechproject.tasks.domain;

import org.motechproject.tasks.contract.ActionParameterRequest;

import java.util.Objects;

import static org.motechproject.tasks.domain.ParameterType.UNICODE;

/**
 * Object representation of a parameter in the channel action definition.
 *
 * @see ActionEvent
 * @since 0.19
 */
public class ActionParameter extends Parameter implements Comparable<ActionParameter> {
    private static final long serialVersionUID = 8204529887802399508L;

    private Integer order;
    private String key;
    private boolean required;

    public ActionParameter() {
        this(null, null, UNICODE, null, true);
    }

    public ActionParameter(String displayName, String key) {
        this(displayName, key, UNICODE, null, true);
    }

    public ActionParameter(String displayName, String key, boolean required) {
        this(displayName, key, UNICODE, null, required);
    }

    public ActionParameter(String displayName, String key, Integer order) {
        this(displayName, key, UNICODE, order, true);
    }

    public ActionParameter(String displayName, String key, ParameterType type) {
        this(displayName, key, type, null, true);
    }

    public ActionParameter(String displayName, String key, ParameterType type, boolean required) {
        this(displayName, key, type, null, required);
    }

    public ActionParameter(String displayName, String key, ParameterType type, Integer order) {
        this(displayName, key, type, order, true);
    }

    public ActionParameter(ActionParameterRequest actionParameterRequest) {
        this(
                actionParameterRequest.getDisplayName(),
                actionParameterRequest.getKey(),
                ParameterType.fromString(actionParameterRequest.getType()),
                actionParameterRequest.getOrder(),
                actionParameterRequest.isRequired()
        );
    }

    public ActionParameter(String displayName, String key, ParameterType type, Integer order,
                           boolean required) {
        super(displayName, type);

        this.order = order;
        this.key = key;
        this.required = required;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public int compareTo(ActionParameter o) {
        return Integer.compare(this.order, o.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key, required);
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
                && Objects.equals(this.required, other.required);
    }

    @Override
    public String toString() {
        return String.format(
                "ActionParameter{order=%d, key='%s', required=%s} %s",
                order, key, required, super.toString()
        );
    }
}
