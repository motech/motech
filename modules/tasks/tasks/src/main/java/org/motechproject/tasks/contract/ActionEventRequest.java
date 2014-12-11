package org.motechproject.tasks.contract;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

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

    public ActionEventRequest() {
        this(null, null, null, null, null, null, null, null);
    }

    public ActionEventRequest(String name, String displayName, String subject, String description,
                              String serviceInterface, String serviceMethod, String serviceMethodCallManner,
                              SortedSet<ActionParameterRequest> actionParameters) {
        this.name = name;
        this.displayName = displayName;
        this.subject = subject;
        this.description = description;
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
        this.serviceMethodCallManner = isBlank(serviceMethodCallManner) ?
                NAMED_PARAMETERS : serviceMethodCallManner;
        this.actionParameters = null == actionParameters ? new TreeSet<ActionParameterRequest>() : actionParameters;
    }

    public String getName() {
        return name;
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

    public String getServiceMethodCallManner() {
        return serviceMethodCallManner;
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
        return Objects.hash(actionParameters, serviceInterface, serviceMethod, serviceMethodCallManner);
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
                equalsService(other.serviceInterface, other.serviceMethod, other.serviceMethodCallManner);

    }

    @Override
    public String toString() {
        return "ActionEventRequest{" +
                "name='" + name + '\'' +
                ", actionParameters=" + actionParameters +
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
