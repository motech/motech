package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ActionEvent extends TaskEvent {
    private static final long serialVersionUID = 8362330377208460896L;

    private List<ActionParameter> actionParameters;
    private String serviceInterface;
    private String serviceMethod;

    public ActionEvent() {
        this(null, null, null, null, null, null);
    }

    public ActionEvent(String description, String displayName, String subject, List<ActionParameter> actionParameters) {
        this(description, displayName, subject, actionParameters, null, null);
    }

    public ActionEvent(String description, String displayName, List<ActionParameter> actionParameters,
                       String serviceInterface, String serviceMethod) {
        this(description, displayName, null, actionParameters, serviceInterface, serviceMethod);
    }

    public ActionEvent(String description, String displayName, String subject, List<ActionParameter> actionParameters,
                       String serviceInterface, String serviceMethod) {
        super(description, displayName, subject);

        this.actionParameters = actionParameters;
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
    }

    @JsonIgnore
    public boolean accept(TaskActionInformation info) {
        boolean result = false;

        if (hasService() && info.hasService() && serviceEquals(info.getServiceInterface(), info.getServiceMethod())) {
            result = true;
        } else if (hasSubject() && info.hasSubject() && subjectEquals(info.getSubject())) {
            result = true;
        }

        return result;
    }

    public boolean hasService() {
        return isNotBlank(serviceInterface) && isNotBlank(serviceMethod);
    }

    public List<ActionParameter> getActionParameters() {
        return actionParameters;
    }

    public void setActionParameters(List<ActionParameter> actionParameters) {
        this.actionParameters = actionParameters;
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
                serviceEquals(other.serviceInterface, other.serviceMethod);

    }

    @Override
    public String toString() {
        return String.format("ActionEvent{actionParameters=%s, serviceInterface='%s', serviceMethod='%s'}",
                actionParameters, serviceInterface, serviceMethod);
    }

    private boolean serviceEquals(String serviceInterface, String serviceMethod) {
        return Objects.equals(this.serviceInterface, serviceInterface) && Objects.equals(this.serviceMethod, serviceMethod);
    }
}
