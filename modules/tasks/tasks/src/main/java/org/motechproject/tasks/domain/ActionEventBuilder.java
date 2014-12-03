package org.motechproject.tasks.domain;

import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * The <code>ActionEventBuilder</code> class provides methods for constructing action events.
 *
 * @see org.motechproject.tasks.domain.ActionEvent
 */
public class ActionEventBuilder {
    private String name;
    private String displayName;
    private String subject;
    private String description;
    private String serviceInterface;
    private String serviceMethod;
    private SortedSet<ActionParameter> actionParameters;
    private MethodCallManner serviceMethodCallManner = MethodCallManner.NAMED_PARAMETERS;

    public ActionEventBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ActionEventBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ActionEventBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public ActionEventBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ActionEventBuilder setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public ActionEventBuilder setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
        return this;
    }

    public ActionEventBuilder setActionParameters(SortedSet<ActionParameter> actionParameters) {
        this.actionParameters = actionParameters;
        return this;
    }

    public ActionEventBuilder setServiceMethodCallManner(MethodCallManner serviceMethodCallManner) {
        this.serviceMethodCallManner = serviceMethodCallManner;
        return this;
    }

    public ActionEvent createActionEvent() {
        return new ActionEvent(name, description, displayName, subject, serviceInterface, serviceMethod,
                serviceMethodCallManner, actionParameters);
    }

    public static ActionEventBuilder fromActionEventRequest(ActionEventRequest actionEventRequest) {
        ActionEventBuilder builder = new ActionEventBuilder();
        builder.setName(actionEventRequest.getName());
        builder.setDescription(actionEventRequest.getDescription());
        builder.setDisplayName(actionEventRequest.getDisplayName());
        builder.setSubject(actionEventRequest.getSubject());
        builder.setServiceInterface(actionEventRequest.getServiceInterface());
        builder.setServiceMethod(actionEventRequest.getServiceMethod());
        builder.setServiceMethodCallManner(getServiceMethodCallManner(actionEventRequest.getServiceMethodCallManner()));
        builder.setActionParameters(mapActionParameters(actionEventRequest.getActionParameters()));
        return builder;
    }

    private static MethodCallManner getServiceMethodCallManner(String methodCallManner) {
        return isBlank(methodCallManner) ? MethodCallManner.NAMED_PARAMETERS : MethodCallManner.valueOf(methodCallManner);
    }

    private static SortedSet<ActionParameter> mapActionParameters(SortedSet<ActionParameterRequest> actionParameterRequests) {
        SortedSet<ActionParameter> actionParameters = new TreeSet<>();
        for (ActionParameterRequest actionParameterRequest : actionParameterRequests) {
            actionParameters.add(ActionParameterBuilder.fromActionParameterRequest(actionParameterRequest)
                    .createActionParameter());
        }
        return actionParameters;
    }

    public static ActionEventBuilder fromActionEvent(ActionEvent actionEventRequest) {
        ActionEventBuilder builder = new ActionEventBuilder();
        builder.setName(actionEventRequest.getName());
        builder.setDescription(actionEventRequest.getDescription());
        builder.setDisplayName(actionEventRequest.getDisplayName());
        builder.setSubject(actionEventRequest.getSubject());
        builder.setServiceInterface(actionEventRequest.getServiceInterface());
        builder.setServiceMethod(actionEventRequest.getServiceMethod());
        builder.setServiceMethodCallManner(actionEventRequest.getServiceMethodCallManner());
        builder.setActionParameters(copyActionParameters(actionEventRequest.getActionParameters()));
        return builder;
    }

    private static SortedSet<ActionParameter> copyActionParameters(SortedSet<ActionParameter> actionParameters) {
        SortedSet<ActionParameter> copiedActionParameters = new TreeSet<>();
        for (ActionParameter actionParameter : actionParameters) {
            copiedActionParameters.add(ActionParameterBuilder.fromActionParameter(actionParameter)
                    .createActionParameter());
        }
        return copiedActionParameters;
    }
}
