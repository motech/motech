package org.motechproject.tasks.domain;

import org.motechproject.mds.annotations.Entity;

@Entity
public class TaskTriggerInformation extends TaskEventInformation {

    private static final long serialVersionUID = 2024337448953130758L;

    public TaskTriggerInformation() {
        this(null, null, null, null, null);
    }

    public TaskTriggerInformation(String displayName, String channelName, String moduleName,
                                  String moduleVersion, String subject) {
        super(displayName, channelName, moduleName, moduleVersion, subject);
    }

    public TaskTriggerInformation(TaskTriggerInformation other) {
        this(other.getDisplayName(), other.getChannelName(), other.getModuleName(), other.getModuleVersion(), other.getSubject());
    }
}
