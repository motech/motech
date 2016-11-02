package org.motechproject.tasks.domain.mds.channel.builder;

import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.enums.MethodCallManner;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * The <code>ActionEventBuilder</code> class provides methods for constructing action events.
 *
 * @see ActionEvent
 */
public class ActionEventBuilder {

    private String name;
    private String displayName;
    private String subject;
    private String description;
    private String serviceInterface;
    private String serviceMethod;
    private SortedSet<ActionParameter> actionParameters;
    private SortedSet<ActionParameter> postActionParameters;
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

    public ActionEventBuilder setPostActionParameters(SortedSet<ActionParameter> postActionParameters) {
        this.postActionParameters = postActionParameters;
        return this;
    }

    public ActionEventBuilder setServiceMethodCallManner(MethodCallManner serviceMethodCallManner) {
        this.serviceMethodCallManner = serviceMethodCallManner;
        return this;
    }

    /**
     * Builds an object of the {@code ActionEvent} class.
     *
     * @return the created instance
     */
    public ActionEvent build() {
        return new ActionEvent(name, description, displayName, subject, serviceInterface, serviceMethod,
                serviceMethodCallManner, actionParameters, postActionParameters);
    }

    /**
     * Builds an object of the {@code ActionEventBuilder} class based on the passed {@code ActionEventRequest} instance.
     *
     * @param actionEventRequest  the action event request, not null
     * @return the instance of the {@code ActionEventBuilder} class ready to build instance of the {@code ActionEvent} class
     */
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
        builder.setPostActionParameters(mapActionParameters(actionEventRequest.getPostActionParameters()));

        return builder;
    }

    private static MethodCallManner getServiceMethodCallManner(String methodCallManner) {
        return isBlank(methodCallManner) ? MethodCallManner.NAMED_PARAMETERS : MethodCallManner.valueOf(methodCallManner);
    }

    private static SortedSet<ActionParameter> mapActionParameters(SortedSet<ActionParameterRequest> actionParameterRequests) {
        SortedSet<ActionParameter> actionParameters = new TreeSet<>();
        for (ActionParameterRequest actionParameterRequest : actionParameterRequests) {
            actionParameters.add(ActionParameterBuilder.fromActionParameterRequest(actionParameterRequest).build());
        }
        return actionParameters;
    }
}
