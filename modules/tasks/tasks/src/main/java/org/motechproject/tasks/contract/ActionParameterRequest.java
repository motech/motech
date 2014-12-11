package org.motechproject.tasks.contract;

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
    private String value;
    private String displayName;
    private String type;
    private boolean required;
    private boolean hidden;

    public ActionParameterRequest() {
        this(null, null, null, null, null, true, false);
    }

    public ActionParameterRequest(Integer order, String key, String value, String displayName, String type,
                                  boolean required, boolean hidden) {
        this.key = key;
        this.value = value;
        this.displayName = displayName;
        this.order = order;
        this.type = isBlank(type) ? UNICODE : type;
        this.required = required;
        this.hidden = hidden;
    }

    public Integer getOrder() {
        return order;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
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

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public int compareTo(ActionParameterRequest o) {
        return Integer.compare(this.order, o.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key, value, displayName, type, required, hidden);
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
                && Objects.equals(this.value, other.value)
                && Objects.equals(this.displayName, other.displayName)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.required, other.required)
                && Objects.equals(this.hidden, other.hidden);
    }

    @Override
    public String toString() {
        return "ActionParameterRequest{" +
                "order=" + order +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type='" + type + '\'' +
                ", required=" + required +
                ", hidden=" + hidden +
                '}';
    }
}
