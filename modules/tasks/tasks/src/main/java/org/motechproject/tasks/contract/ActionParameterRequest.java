package org.motechproject.tasks.contract;

import java.util.Objects;
import java.util.SortedSet;

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
    private SortedSet<String> options;

    /**
     * Constructor.
     */
    public ActionParameterRequest() {
        this(null, null, null, null, null, true, false, null);
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
     * @param options the parameter options for select parameter type
     */
    public ActionParameterRequest(Integer order, String key, String value, String displayName, String type,
                                  boolean required, boolean hidden, SortedSet<String> options) {
        this.key = key;
        this.value = value;
        this.displayName = displayName;
        this.order = order;
        this.type = isBlank(type) ? UNICODE : type;
        this.required = required;
        this.hidden = hidden;
        this.options = options;
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
     * Returns the options of the parameter.
     *
     * @return  options of the parameter order
     */
    public SortedSet<String> getOptions() {
        return options;
    }

    /**
     * Sets the options of the parameter.
     *
     * @param options of the parameter order
     */
    public void setOptions(SortedSet<String> options) {
        this.options = options;
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
        return Objects.hash(order, key, value, displayName, type, required, hidden, options);
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

        final ActionParameterRequest other = (ActionParameterRequest) obj;

        return Objects.equals(this.order, other.order)
                && Objects.equals(this.key, other.key)
                && Objects.equals(this.value, other.value)
                && Objects.equals(this.displayName, other.displayName)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.required, other.required)
                && Objects.equals(this.hidden, other.hidden)
                && Objects.equals(this.options, other.options);
    }

    // CHECKSTYLE:ON
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
                ", options="+ options+
                '}';
    }
}
