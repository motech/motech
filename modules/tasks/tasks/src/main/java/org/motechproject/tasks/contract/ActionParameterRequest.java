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

    /**
     * Constructor.
     */
    public ActionParameterRequest() {
        this(null, null, null, null, null, true, false);
    }

    /**
     * Constructor.
     *
     * @param order  the parameter order
     * @param key  the parameter key
     * @param value  the parameter value
     * @param displayName  the parameter display name
     * @param type  the parameter type
     * @param required  defines if the parameter is required
     * @param hidden  defines if the parameter is hidden on the UI
     */
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

    /**
     * Returns the order of the parameter.
     *
     * @return the parameter order
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * Returns the key of the parameter.
     *
     * @return the parameter key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value of the parameter.
     *
     * @return the parameter value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the display name of the parameter.
     *
     * @return the parameter display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the type of the parameter.
     *
     * @return the parameter type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the order of the parameter.
     *
     * @param order  the parameter order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Returns whether this action parameter is required.
     *
     * @return true if this action parameter is required, false otherwise
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Returns whether this action parameter should be hidden on the UI.
     *
     * @return  true if this action parameter should be hidden on the UI, false otherwise
     */
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
