package org.motechproject.tasks.contract;

import org.motechproject.tasks.domain.mds.channel.ActionEvent;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Service layer object denoting a {@link ActionEvent}. It is a part of the
 * {@link org.motechproject.tasks.contract.ChannelRequest} and is used by
 * {@link org.motechproject.tasks.service.ChannelService} for adding new or updating already existent action events.
 */
public class ActionEventRequest {

    private static final String NAMED_PARAMETERS = "NAMED_PARAMETERS";

    private String name;
    private String displayName;
    private String subject;
    private String description;
    private String serviceInterface;
    private String serviceMethod;
    private String serviceMethodCallManner;
    private SortedSet<ActionParameterRequest> actionParameters;
    private SortedSet<ActionParameterRequest> postActionParameters;

    /**
     * Constructor.
     */
    public ActionEventRequest() {
        this(null, null, null, null, null, null, null, null, null);
    }

    /**
     * Constructor.
     */
    public ActionEventRequest(String name, String displayName, String subject, String description,
                              String serviceInterface, String serviceMethod, String serviceMethodCallManner,
                              SortedSet<ActionParameterRequest> actionParameters) {

         this(name, displayName, subject, description, serviceInterface, serviceMethod, serviceMethodCallManner, actionParameters, null);
    }

    /**
     * Constructor.
     *
     * @param name  the event name
     * @param displayName  the event display name
     * @param subject  the event subject
     * @param description  the event description
     * @param serviceInterface  the event service interface
     * @param serviceMethod  the event service method
     * @param serviceMethodCallManner  the event service method call manner, for supported values check {@see org.motechproject.tasks.domain.enums.MethodCallManner}
     * @param actionParameters  the action parameters
     */
    public ActionEventRequest(String name, String displayName, String subject, String description,
                              String serviceInterface, String serviceMethod, String serviceMethodCallManner,
                              SortedSet<ActionParameterRequest> actionParameters, SortedSet<ActionParameterRequest> postActionParameters) {
        this.name = name;
        this.displayName = displayName;
        this.subject = subject;
        this.description = description;
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
        this.serviceMethodCallManner = isBlank(serviceMethodCallManner) ?
                NAMED_PARAMETERS : serviceMethodCallManner;
        this.actionParameters = null == actionParameters ? new TreeSet<ActionParameterRequest>() : actionParameters;
        this.postActionParameters = null == postActionParameters ? new TreeSet<ActionParameterRequest>() : postActionParameters;
    }

    /**
     * Returns the name of the action event.
     *
     * @return the action event name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the display name of the action event.
     *
     * @return the action event display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the subject of the action event.
     *
     * @return the action event subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the description of the action event.
     *
     * @return the action event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the service interface of the action event.
     *
     * @return the action event service interface
     */
    public String getServiceInterface() {
        return serviceInterface;
    }

    /**
     * Returns the service method of the action event.
     *
     * @return the action event service method
     */
    public String getServiceMethod() {
        return serviceMethod;
    }

    /**
     * Returns the service method call manner of the action event.
     *
     * @return the action event service method call manner
     */
    public String getServiceMethodCallManner() {
        return serviceMethodCallManner;
    }

    /**
     * Returns the action parameters.
     *
     * @return the action parameters
     */
    public SortedSet<ActionParameterRequest> getActionParameters() {
        return actionParameters;
    }

    /**
     * Returns the post action parameters.
     *
     * @return the post action parameters
     */
    public SortedSet<ActionParameterRequest> getPostActionParameters() {
        return postActionParameters;
    }

    /**
     * Adds the given parameter request to the list of stored parameter requests.
     *
     * @param parameter  the action parameter request
     * @param changeOrder  defines if order of the given parameter should continue numeration of the stored list
     */
    public void addParameter(ActionParameterRequest parameter, boolean changeOrder) {
        if (changeOrder) {
            if (actionParameters.isEmpty()) {
                parameter.setOrder(0);
            } else {
                parameter.setOrder(actionParameters.last().getOrder() + 1);
            }
        }
        actionParameters.add(parameter);
    }

    /**
     * Adds the given parameter request to the list of stored post action parameter requests.
     *
     * @param parameter  the post action parameter request
     * @param changeOrder  defines if order of the given parameter should continue numeration of the stored list
     */
    public void addPostActionParameter(ActionParameterRequest parameter, boolean changeOrder) {
        if (changeOrder) {
            if (postActionParameters.isEmpty()) {
                parameter.setOrder(0);
            } else {
                parameter.setOrder(postActionParameters.last().getOrder() + 1);
            }
        }
        postActionParameters.add(parameter);
    }

    /**
     * Checks if this action event request has subject or service defined.
     *
     * @return true if this has subject or service set, false otherwise
     */
    public boolean isValid() {
        return hasSubject() || hasService();
    }

    /**
     * Checks if this action event request has subject.
     *
     * @return true if action event has subject, false otherwise
     */
    public boolean hasSubject() {
        return isNotBlank(subject);
    }

    /**
     * Checks if this action event request has service interface and method specified.
     *
     * @return true if action has service interface and method specified, false otherwise
     */
    public boolean hasService() {
        return isNotBlank(serviceInterface) && isNotBlank(serviceMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionParameters, postActionParameters, serviceInterface, serviceMethod, serviceMethodCallManner);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ActionEventRequest other = (ActionEventRequest) obj;

        return Objects.equals(this.actionParameters, other.actionParameters) &&
                Objects.equals(this.postActionParameters, other.postActionParameters) &&
                equalsService(other.serviceInterface, other.serviceMethod, other.serviceMethodCallManner);
    }

    @Override
    public String toString() {
        return "ActionEventRequest{" +
                "name='" + name + '\'' +
                ", actionParameters=" + actionParameters +
                ", postActionParameters=" + postActionParameters+
                ", serviceInterface='" + serviceInterface + '\'' +
                ", serviceMethod='" + serviceMethod + '\'' +
                ", serviceMethodCallManner='" + serviceMethodCallManner + '\'' +
                '}';
    }

    private boolean equalsService(String serviceInterface, String serviceMethod, String serviceMethodCallManner) {
        return Objects.equals(this.serviceInterface, serviceInterface) &&
                Objects.equals(this.serviceMethod, serviceMethod) &&
                Objects.equals(this.serviceMethodCallManner, serviceMethodCallManner);
    }
}
