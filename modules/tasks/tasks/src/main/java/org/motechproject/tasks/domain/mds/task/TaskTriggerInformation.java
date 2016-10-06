package org.motechproject.tasks.domain.mds.task;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.TaskTriggerInformationDto;

/**
 * Represents information about a single task trigger. A task trigger is an event that triggers execution of a task. It
 * is a part of the task model.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class TaskTriggerInformation extends TaskEventInformation {

    private static final long serialVersionUID = 2024337448953130758L;

    @Field
    private String triggerListenerSubject;

    /**
     * Constructor.
     */
    public TaskTriggerInformation() {
        this(null, null, null, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the trigger display name
     * @param channelName  the trigger channel name
     * @param moduleName  the trigger module name
     * @param moduleVersion  the module version
     * @param subject  the trigger subject
     * @param triggerListener  the trigger listener
     */
    public TaskTriggerInformation(String displayName, String channelName, String moduleName,
                                  String moduleVersion, String subject, String triggerListener) {
        super(null, displayName, channelName, moduleName, moduleVersion, subject);
        this.triggerListenerSubject = StringUtils.isEmpty(triggerListener) ? subject : triggerListener;
    }

    /**
     * The copy constructor.
     *
     * @param other  the other {@code TaskTrigger} to copy, not null
     */
    public TaskTriggerInformation(TaskTriggerInformation other) {
        this(other.getDisplayName(), other.getChannelName(), other.getModuleName(), other.getModuleVersion(), other.getSubject(), other.getTriggerListenerSubject());
    }


    public String getTriggerListenerSubject() {
        return triggerListenerSubject;
    }

    /**
     * Convenient method for determining effective listener subject. For tasks created prior release 0.25
     * the trigger listener subject will not be set in the db, therefore we have to use subject.
     *
     * @return <code>triggerListenerSubject</code> if present. Otherwise returns <code>subject</code>
     */
    @Ignore
    @JsonIgnore
    public String getEffectiveListenerSubject() {
        return StringUtils.isEmpty(triggerListenerSubject) ? super.getSubject() : triggerListenerSubject;
    }

    /**
     * Convenient method for determining effective listener subject for task retry.
     *
     * @return the listener retry subject
     */
    @Ignore
    @JsonIgnore
    public String getEffectiveListenerRetrySubject() {
        return getEffectiveListenerSubject() + ".handleRetry";
    }

    public TaskTriggerInformationDto toDto() {
        return new TaskTriggerInformationDto(getName(), getDisplayName(), getChannelName(), getModuleName(),
                getModuleVersion(), getSubject(), triggerListenerSubject);
    }
}
