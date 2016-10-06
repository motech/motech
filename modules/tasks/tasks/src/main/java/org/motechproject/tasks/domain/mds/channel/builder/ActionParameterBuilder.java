package org.motechproject.tasks.domain.mds.channel.builder;

import org.motechproject.tasks.contract.ActionParameterRequest;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;

import java.util.SortedSet;

/**
 * The <code>ActionParameterBuilder</code> class provides methods for constructing action parameters.
 *
 * @see ActionParameter
 */
public class ActionParameterBuilder {
    private String displayName;
    private String key;
    private ParameterType type = ParameterType.UNICODE;
    private Integer order;
    private boolean required = true;
    private String value;
    private boolean hidden;
    private SortedSet<String> options;

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

    public ActionParameterBuilder setOptions(SortedSet<String> options){
        this.options = options;
        return  this;
    }

    /**
     * Builds an object of the {@code ActionParameter} class.
     *
     * @return the created instance
     */
    public ActionParameter build() {
        return new ActionParameter(displayName, type, order, key, value, required, hidden, options);
    }

    /**
     * Builds an object of the {@code ActionParameterBuilder} class based on the passed {@code ActionParameterRequest}
     * instance.
     *
     * @param actionParameterRequest  the action parameter request, not null
     * @return a builder ready to build a new instance of the {@code ActionParameter} class
     */
    public static ActionParameterBuilder fromActionParameterRequest(ActionParameterRequest actionParameterRequest) {
        ActionParameterBuilder builder = new ActionParameterBuilder();
        builder.setDisplayName(actionParameterRequest.getDisplayName());
        builder.setType(ParameterType.fromString(actionParameterRequest.getType()));
        builder.setOrder(actionParameterRequest.getOrder());
        builder.setKey(actionParameterRequest.getKey());
        builder.setValue(actionParameterRequest.getValue());
        builder.setRequired(actionParameterRequest.isRequired());
        builder.setHidden(actionParameterRequest.isHidden());
        builder.setOptions(actionParameterRequest.getOptions());
        return builder;
    }
}
