package org.motechproject.tasks.domain;

import org.motechproject.tasks.contract.ActionParameterRequest;

/**
 * The <code>ActionParameterBuilder</code> class provides methods for constructing action parameters.
 *
 * @see org.motechproject.tasks.domain.ActionParameter
 */
public class ActionParameterBuilder {
    private String displayName;
    private String key;
    private ParameterType type = ParameterType.UNICODE;
    private Integer order;
    private boolean required = true;
    private String value;
    private boolean hidden;

    public ActionParameterBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ActionParameterBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public ActionParameterBuilder setType(ParameterType type) {
        this.type = type;
        return this;
    }

    public ActionParameterBuilder setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public ActionParameterBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public ActionParameterBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public ActionParameterBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ActionParameter createActionParameter() {
        return new ActionParameter(displayName, type, order, key, value, required, hidden);
    }

    public static ActionParameterBuilder fromActionParameterRequest(ActionParameterRequest actionParameterRequest) {
        ActionParameterBuilder builder = new ActionParameterBuilder();
        builder.setDisplayName(actionParameterRequest.getDisplayName());
        builder.setType(ParameterType.fromString(actionParameterRequest.getType()));
        builder.setOrder(actionParameterRequest.getOrder());
        builder.setKey(actionParameterRequest.getKey());
        builder.setValue(actionParameterRequest.getValue());
        builder.setRequired(actionParameterRequest.isRequired());
        builder.setHidden(actionParameterRequest.isHidden());
        return builder;
    }

    public static ActionParameterBuilder fromActionParameter(ActionParameter actionParameter) {
        ActionParameterBuilder builder = new ActionParameterBuilder();
        builder.setDisplayName(actionParameter.getDisplayName());
        builder.setType(actionParameter.getType());
        builder.setOrder(actionParameter.getOrder());
        builder.setKey(actionParameter.getKey());
        builder.setValue(actionParameter.getValue());
        builder.setRequired(actionParameter.isRequired());
        builder.setHidden(actionParameter.isHidden());
        return builder;
    }
}
