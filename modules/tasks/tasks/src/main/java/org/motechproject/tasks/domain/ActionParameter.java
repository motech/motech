package org.motechproject.tasks.domain;

import java.util.Objects;

import static org.motechproject.tasks.domain.ParameterType.UNICODE;

public class ActionParameter extends Parameter implements Comparable<ActionParameter> {
    private static final long serialVersionUID = 8204529887802399508L;

    private Integer order;
    private String key;

    public ActionParameter() {
        this(null, null, UNICODE, null);
    }

    public ActionParameter(String displayName, String key) {
        this(displayName, key, UNICODE, null);
    }

    public ActionParameter(String displayName, String key, Integer order) {
        this(displayName, key, UNICODE, order);
    }

    public ActionParameter(String displayName, String key, ParameterType type) {
        this(displayName, key, type, null);
    }

    public ActionParameter(String displayName, String key, ParameterType type, Integer order) {
        super(displayName, type);

        this.order = order;
        this.key = key;
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

    @Override
    public int compareTo(ActionParameter o) {
        return Integer.compare(this.order, o.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key);
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

        return Objects.equals(this.order, other.order) &&
                Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        return String.format("ActionParameter{order=%d, key='%s'}", order, key);
    }
}
