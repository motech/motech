package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.tasks.dto.TaskEventInformationDto;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Represents information about single task event. Task event is an abstract base for events utilized in the task
 * module. It serves as a base for both {@link TaskActionInformation}s and {@link TaskTriggerInformation}s. It is a part
 * of the task model.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
public abstract class TaskEventInformation implements Serializable {
    private static final long serialVersionUID = -4931626162036319942L;

    @Field
    private String name;

    @Field
    private String displayName;

    @Field
    private String channelName;

    @Field
    private String moduleName;

    @Field
    private String moduleVersion;

    @Field
    private String subject;

    /**
     * Constructor.
     */
    public TaskEventInformation() {
        this(null, null, null, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param name  the event name
     * @param displayName  the event display name
     * @param channelName  the event channel name
     * @param moduleName  the event module name
     * @param moduleVersion  the module version
     * @param subject  the event subject
     */
    public TaskEventInformation(String name, String displayName, String channelName, String moduleName, String moduleVersion, String subject) {
        this.name = name;
        this.displayName = displayName;
        this.channelName = channelName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.subject = subject;
    }

    public abstract TaskEventInformationDto toDto();

    public boolean hasSubject() {
        return isNotBlank(subject);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
