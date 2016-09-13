package org.motechproject.tasks.dto;

import java.util.Map;

public class TaskActionInformationDto extends TaskEventInformationDto {

    private String serviceInterface;
    private String serviceMethod;
    private Map<String, String> values;

    public TaskActionInformationDto(String name, String displayName, String channelName, String moduleName,
                                    String moduleVersion, String subject, String serviceInterface, String serviceMethod, Map<String, String> values) {
        super(name, displayName, channelName, moduleName, moduleVersion, subject);
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
        this.values = values;
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

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
