package org.motechproject.tasks.dto;

import org.motechproject.tasks.domain.enums.MethodCallManner;

import java.util.SortedSet;

public class ActionEventDto {

    private String name;
    private String description;
    private String displayName;
    private String subject;
    private SortedSet<ActionParameterDto> actionParameters;
    private String serviceInterface;
    private String serviceMethod;
    private MethodCallManner serviceMethodCallManner;
    private SortedSet<ActionParameterDto> postActionParameters;

    public ActionEventDto(String name, String description, String displayName, String subject,
                          SortedSet<ActionParameterDto> actionParameters, String serviceInterface, String serviceMethod,
                          MethodCallManner serviceMethodCallManner, SortedSet<ActionParameterDto> postActionParameters) {
        this.name = name;
        this.description = description;
        this.displayName = displayName;
        this.subject = subject;
        this.actionParameters = actionParameters;
        this.serviceInterface = serviceInterface;
        this.serviceMethod = serviceMethod;
        this.serviceMethodCallManner = serviceMethodCallManner;
        this.postActionParameters = postActionParameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public SortedSet<ActionParameterDto> getActionParameters() {
        return actionParameters;
    }

    public void setActionParameters(SortedSet<ActionParameterDto> actionParameters) {
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

    public MethodCallManner getServiceMethodCallManner() {
        return serviceMethodCallManner;
    }

    public void setServiceMethodCallManner(MethodCallManner serviceMethodCallManner) {
        this.serviceMethodCallManner = serviceMethodCallManner;
    }

    public SortedSet<ActionParameterDto> getPostActionParameters() {
        return postActionParameters;
    }

    public void setPostActionParameters(SortedSet<ActionParameterDto> postActionParameters) {
        this.postActionParameters = postActionParameters;
    }
}
