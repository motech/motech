package org.motechproject.tasks.dto;

public class TaskTriggerInformationDto extends TaskEventInformationDto {

    private String triggerListenerSubject;

    public TaskTriggerInformationDto(String name, String displayName, String channelName, String moduleName,
                                     String moduleVersion, String subject, String triggerListenerSubject) {
        super(name, displayName, channelName, moduleName, moduleVersion, subject);
        this.triggerListenerSubject = triggerListenerSubject;
    }

    public String getTriggerListenerSubject() {
        return triggerListenerSubject;
    }

    public void setTriggerListenerSubject(String triggerListenerSubject) {
        this.triggerListenerSubject = triggerListenerSubject;
    }
}
