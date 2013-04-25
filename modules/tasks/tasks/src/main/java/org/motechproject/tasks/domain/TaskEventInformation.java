package org.motechproject.tasks.domain;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class TaskEventInformation implements Serializable {
    private static final long serialVersionUID = -4931626162036319942L;

    private String displayName;
    private String channelName;
    private String moduleName;
    private String moduleVersion;
    private String subject;

    public TaskEventInformation() {
        this(null, null, null, null, null);
    }

    public TaskEventInformation(String displayName, String channelName, String moduleName, String moduleVersion, String subject) {
        this.displayName = displayName;
        this.channelName = channelName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.subject = subject;
    }

    public boolean hasSubject() {
        return isNotBlank(subject);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, channelName, moduleName, moduleVersion, subject);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskEventInformation other = (TaskEventInformation) obj;

        return Objects.equals(this.displayName, other.displayName) &&
                Objects.equals(this.channelName, other.channelName) &&
                Objects.equals(this.moduleName, other.moduleName) &&
                Objects.equals(this.moduleVersion, other.moduleVersion) &&
                Objects.equals(this.subject, other.subject);
    }

    @Override
    public String toString() {
        return String.format("TaskEventInformation{displayName='%s', channelName='%s', moduleName='%s', moduleVersion='%s', subject='%s'}",
                displayName, channelName, moduleName, moduleVersion, subject);
    }
}
