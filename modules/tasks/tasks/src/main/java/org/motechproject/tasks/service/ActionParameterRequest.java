package org.motechproject.tasks.service;

import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Object representation of a parameter in the channel action request definition.
 *
 * @see ActionEventRequest
 * @since 0.21
 */
public class ActionParameterRequest implements Comparable<ActionParameterRequest> {

    private static final String UNICODE = "UNICODE";

    private Integer order;
    private String key;
    private String displayName;
    private String type;
    private boolean required;

    private ActionParameterRequest() {
        this(null, null, null, UNICODE, true);
    }

    public ActionParameterRequest(String key, String displayName, Integer order, String type,
                                  boolean required) {
        this.key = key;
        this.displayName = displayName;
        this.order = order;
        this.type = isBlank(type) ? UNICODE : type;
        this.required = required;
    }

    public ActionParameterRequest(String key, String displayName, Integer order, String type) {
        this(key, displayName, order, type, true);
    }

    public ActionParameterRequest(String key, String displayName, Integer order) {
        this(key, displayName, order, null);
    }

    public ActionParameterRequest(String key, String displayName) {
        this(key, displayName, null, null);
    }

    public Integer getOrder() {
        return order;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public int compareTo(ActionParameterRequest o) {
        return Integer.compare(this.order, o.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key, displayName, type, required);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ActionParameterRequest other = (ActionParameterRequest) obj;

        return Objects.equals(this.order, other.order)
                && Objects.equals(this.key, other.key)
                && Objects.equals(this.displayName, other.displayName)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.required, other.required);
    }

    @Override
    public String toString() {
        return String.format(
                "ActionParameter{order=%d, key='%s', displayName='%s', type='%s', required=%s}",
                order, key, displayName, type, required
        );
    }
}
