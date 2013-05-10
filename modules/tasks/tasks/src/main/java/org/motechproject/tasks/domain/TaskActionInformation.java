package org.motechproject.tasks.domain;

import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class TaskActionInformation extends TaskEventInformation {
    private static final long serialVersionUID = -132464255615128442L;

    private String serviceInterface;
    private String serviceMethod;

    public TaskActionInformation() {
        this(null, null, null, null, null);
    }

    public TaskActionInformation(String displayName, String channelName, String moduleName, String moduleVersion, String subject) {
        super(displayName, channelName, moduleName, moduleVersion, subject);
    }

    public TaskActionInformation(String displayName, String channelName, String moduleName, String moduleVersion,
                                 String serviceInterface, String serviceMethod) {
        super(displayName, channelName, moduleName, moduleVersion, null);
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
    }

    public boolean hasService() {
        return isNotBlank(serviceInterface) && isNotBlank(serviceMethod);
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
        return Objects.hash(serviceInterface, serviceMethod);
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

        final TaskActionInformation other = (TaskActionInformation) obj;

        return Objects.equals(this.serviceInterface, other.serviceInterface) &&
                Objects.equals(this.serviceMethod, other.serviceMethod);
    }

    @Override
    public String toString() {
        return String.format("TaskActionInformation{serviceInterface='%s', serviceMethod='%s'} %s",
                serviceInterface, serviceMethod, super.toString());
    }
}
