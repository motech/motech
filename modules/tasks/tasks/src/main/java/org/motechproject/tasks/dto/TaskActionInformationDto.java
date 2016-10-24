package org.motechproject.tasks.dto;

import java.util.Map;

public class TaskActionInformationDto extends TaskEventInformationDto {

    private String serviceInterface;
    private String serviceMethod;
    private String specifiedName;
    private Map<String, String> values;

    public TaskActionInformationDto(String name, String specifiedName, String displayName, String channelName, String moduleName,
                                    String moduleVersion, String subject, String serviceInterface, String serviceMethod, Map<String, String> values) {
        super(name, displayName, channelName, moduleName, moduleVersion, subject);
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
        this.specifiedName = specifiedName;
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

    public String getSpecifiedName() {
        return specifiedName;
    }

    public void setSpecifiedName(String specifiedName) {
        this.specifiedName = specifiedName;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
