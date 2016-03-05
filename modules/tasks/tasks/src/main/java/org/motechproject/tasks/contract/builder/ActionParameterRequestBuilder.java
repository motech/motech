package org.motechproject.tasks.contract.builder;

import org.motechproject.tasks.contract.ActionParameterRequest;

import java.util.SortedSet;

/**
 * The <code>ActionParameterRequestBuilder</code> class provides methods for constructing action parameter requests.
 *
 * @see org.motechproject.tasks.contract.ActionParameterRequest
 */
public class ActionParameterRequestBuilder {

    private String key;
    private String value;
    private String displayName;
    private Integer order;
    private String type;
    private boolean required;
    private boolean hidden;
    private SortedSet<String> options;

    /**
     * Sets the key of the action parameter to build the request for.
     *
     * @param key  the action parameter key
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Sets the value of the action parameter to build the request for.
     *
     * @param value  the action parameter value
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * Sets the display name of the action parameter to build the request for.
     *
     * @param displayName  the action parameter display name
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Sets the order of the action parameter to build the request for.
     *
     * @param order  the action parameter order
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setOrder(Integer order) {
        this.order = order;
        return this;
    }

    /**
     * Sets the type of the action parameter to build the request for.
     *
     * @param type  the action parameter type
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Defines whether the action parameter, that request will be build for, should be required.
     *
     * @param required  defines if the action parameter should be required
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    /**
     * Defines whether the action parameter, that request will be build for, should be hidden on the UI.
     *
     * @param hidden  defines if the action parameter should be hidden on the UI
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    /**
     * Sets the options of the action parameter to build the request for.
     *
     * @param options  the action parameter options
     * @return the reference to this object
     */
    public ActionParameterRequestBuilder setOptions(SortedSet<String> options){
        this.options = options;
        return this;
    }

    /**
     * Builds an object of the {@code ActionParameterRequest} class.
     *
     * @return the created instance
     */
    public ActionParameterRequest createActionParameterRequest() {
        return new ActionParameterRequest(order, key, value, displayName, type, required, hidden, options);
    }
}
