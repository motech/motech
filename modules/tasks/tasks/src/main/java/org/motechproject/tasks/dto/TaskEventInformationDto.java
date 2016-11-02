package org.motechproject.tasks.dto;

public abstract class TaskEventInformationDto {

    private String name;
    private String displayName;
    private String channelName;
    private String moduleName;
    private String moduleVersion;
    private String subject;

    public TaskEventInformationDto(String name, String displayName, String channelName, String moduleName, String moduleVersion, String subject) {
        this.name = name;
        this.displayName = displayName;
        this.channelName = channelName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public String getSubject() {
        return subject;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
