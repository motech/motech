package org.motechproject.tasks.contract;

import java.util.SortedSet;

/**
 * The <code>ActionEventRequestBuilder</code> class provides methods for constructing action event requests.
 *
 * @see org.motechproject.tasks.contract.ActionEventRequest
 */
public class ActionEventRequestBuilder {
    private String name;
    private String displayName;
    private String subject;
    private String description;
    private String serviceInterface;
    private String serviceMethod;
    private SortedSet<ActionParameterRequest> actionParameters;
    private String serviceMethodCallManner;

    public ActionEventRequestBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ActionEventRequestBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ActionEventRequestBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public ActionEventRequestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ActionEventRequestBuilder setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public ActionEventRequestBuilder setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
        return this;
    }

    public ActionEventRequestBuilder setActionParameters(SortedSet<ActionParameterRequest> actionParameters) {
        this.actionParameters = actionParameters;
        return this;
    }

    public ActionEventRequestBuilder setServiceMethodCallManner(String serviceMethodCallManner) {
        this.serviceMethodCallManner = serviceMethodCallManner;
        return this;
    }

    public ActionEventRequest createActionEventRequest() {
        return new ActionEventRequest(name, displayName, subject, description, serviceInterface, serviceMethod, serviceMethodCallManner, actionParameters);
    }
}