package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ActionEvent extends TaskEvent {
    private static final long serialVersionUID = 8362330377208460896L;

    private SortedSet<ActionParameter> actionParameters;
    private String serviceInterface;
    private String serviceMethod;

    public ActionEvent() {
        this(null, null, null, null, null, null);
    }

    public ActionEvent(String displayName, String subject, String description, SortedSet<ActionParameter> actionParameters) {
        this(displayName, subject, description, null, null, actionParameters);
    }

    public ActionEvent(String displayName, String description, String serviceInterface, String serviceMethod,
                       SortedSet<ActionParameter> actionParameters) {
        this(displayName, null, description, serviceInterface, serviceMethod, actionParameters);
    }

    public ActionEvent(String displayName, String subject, String description, String serviceInterface,
                       String serviceMethod, SortedSet<ActionParameter> actionParameters) {
        super(description, displayName, subject);

        this.actionParameters = actionParameters == null ? new TreeSet<ActionParameter>() : actionParameters;
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
    }

    @JsonIgnore
    public boolean accept(TaskActionInformation info) {
        boolean result = false;

        if (hasService() && info.hasService() && equalsService(info.getServiceInterface(), info.getServiceMethod())) {
            result = true;
        } else if (hasSubject() && info.hasSubject() && equalsSubject(info.getSubject())) {
            result = true;
        }

        return result;
    }

    @Override
    public boolean containsParameter(String key) {
        boolean found = false;

        for (ActionParameter param : getActionParameters()) {
            if (equalsIgnoreCase(param.getKey(), key)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public void addParameter(ActionParameter parameter, boolean changeOrder) {
        if (changeOrder) {
            if (actionParameters.isEmpty()) {
                parameter.setOrder(0);
            } else {
                parameter.setOrder(actionParameters.last().getOrder() + 1);
            }
        }

        actionParameters.add(parameter);
    }

    public boolean hasService() {
        return isNotBlank(serviceInterface) && isNotBlank(serviceMethod);
    }

    public SortedSet<ActionParameter> getActionParameters() {
        return actionParameters;
    }

    public void setActionParameters(SortedSet<ActionParameter> actionParameters) {
        this.actionParameters.clear();

        if (actionParameters != null) {
            this.actionParameters.addAll(actionParameters);
        }
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
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

        if (!super.equals(obj)) {
            return false;
        }

        final ActionEvent other = (ActionEvent) obj;

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
