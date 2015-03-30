package org.motechproject.tasks.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Ignore;

@Entity(recordHistory = true)
public class TaskTriggerInformation extends TaskEventInformation {

    private static final long serialVersionUID = 2024337448953130758L;

    private String triggerListenerSubject;

    public TaskTriggerInformation() {
        this(null, null, null, null, null, null);
    }

    public TaskTriggerInformation(String displayName, String channelName, String moduleName,
                                  String moduleVersion, String subject, String triggerListener) {
        super(null, displayName, channelName, moduleName, moduleVersion, subject);
        this.triggerListenerSubject = StringUtils.isEmpty(triggerListener) ? subject : triggerListener;
    }

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
}
