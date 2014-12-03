package org.motechproject.tasks.contract;

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

    public ActionParameterRequestBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public ActionParameterRequestBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public ActionParameterRequestBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ActionParameterRequestBuilder setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public ActionParameterRequestBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ActionParameterRequestBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public ActionParameterRequestBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ActionParameterRequest createActionParameterRequest() {
        return new ActionParameterRequest(order, key, value, displayName, type, required, hidden);
    }
}
