package org.motechproject.tasks.domain;

import org.motechproject.mds.annotations.Entity;

@Entity
public class TaskTriggerInformation extends TaskEventInformation {

    public TaskTriggerInformation() {
        this(null, null, null, null, null);
    }

    public TaskTriggerInformation(String displayName, String channelName, String moduleName,
                                  String moduleVersion, String subject) {
        super(displayName, channelName, moduleName, moduleVersion, subject);
    }

}
