package org.motechproject.tasks.contract.builder;

import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;

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
    private SortedSet<ActionParameterRequest> postActionParameters;
    private String serviceMethodCallManner;

    /**
     * Sets the name of the action event to be built.
     *
     * @param name  the action event name
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the display name of the action event to be built.
     *
     * @param displayName  the action event display name
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Sets the subject of the action event to be built.
     *
     * @param subject  the action event subject
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Sets the description of the action event to be built.
     *
     * @param description  the action event description
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the service interface of the action event to be built.
     *
     * @param serviceInterface  the action event service interface
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    /**
     * Sets the service method of the action event to be built.
     *
     * @param serviceMethod  the action event service method
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
        return this;
    }

    /**
     * Sets the parameters of the action event to be built.
     *
     * @param actionParameters  the action event parameter
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setActionParameters(SortedSet<ActionParameterRequest> actionParameters) {
        this.actionParameters = actionParameters;
        return this;
    }

    /**
     * Sets the post action parameters of the action event to be built.
     *
     * @param postActionParameters  the post action event parameter
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setPostActionParameters(SortedSet<ActionParameterRequest> postActionParameters) {
        this.postActionParameters = postActionParameters;
        return this;
    }

    /**
     * Sets the service method call manner of the action event to be built.
     *
     * @param serviceMethodCallManner  the action event service method call manner, for supported values see {@see org.motechproject.tasks.domain.enums.MethodCallManner}
     * @return the reference to this object
     */
    public ActionEventRequestBuilder setServiceMethodCallManner(String serviceMethodCallManner) {
        this.serviceMethodCallManner = serviceMethodCallManner;
        return this;
    }

    /**
     * Builds an object of the {@code ActionEventRequest} class.
     *
     * @return the created instance
     */
    public ActionEventRequest createActionEventRequest() {
        return new ActionEventRequest(name, displayName, subject, description, serviceInterface, serviceMethod, serviceMethodCallManner, actionParameters, postActionParameters);
    }
}