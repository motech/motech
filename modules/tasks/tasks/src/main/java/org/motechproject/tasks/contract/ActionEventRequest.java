package org.motechproject.tasks.contract;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ActionEventRequest {

    private String displayName;
    private String subject;
    private String description;
    private String serviceInterface;
    private String serviceMethod;
    private SortedSet<ActionParameterRequest> actionParameters;

    private ActionEventRequest() {
        this.actionParameters = new TreeSet<>();
    }

    public ActionEventRequest(String displayName, String subject, String description, String serviceInterface, String serviceMethod, SortedSet<ActionParameterRequest> actionParameters) {
        this(displayName, subject, description, serviceInterface, serviceMethod);
        this.actionParameters = actionParameters;
    }

    public ActionEventRequest(String displayName, String subject, String description, String serviceInterface, String serviceMethod) {
        this();
        this.displayName = displayName;
        this.subject = subject;
        this.description = description;
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public SortedSet<ActionParameterRequest> getActionParameters() {
        return actionParameters;
    }

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

    public boolean isValid() {
        return hasSubject() || hasService();
    }


    public boolean hasSubject() {
        return isNotBlank(subject);
    }

    public boolean hasService() {
        return isNotBlank(serviceInterface) && isNotBlank(serviceMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionParameters, serviceInterface, serviceMethod);
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
                equalsService(other.serviceInterface, other.serviceMethod);

    }

    @Override
    public String toString() {
        return String.format("ActionEvent{actionParameters=%s, serviceInterface='%s', serviceMethod='%s'}",
                actionParameters, serviceInterface, serviceMethod);
    }

    private boolean equalsService(String serviceInterface, String serviceMethod) {
        return Objects.equals(this.serviceInterface, serviceInterface) &&
                Objects.equals(this.serviceMethod, serviceMethod);
    }
}
