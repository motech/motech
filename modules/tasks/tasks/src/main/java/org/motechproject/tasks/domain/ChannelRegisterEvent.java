package org.motechproject.tasks.domain;

import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.events.constants.EventDataKeys;
import org.motechproject.tasks.events.constants.EventSubjects;

import java.util.HashMap;

/**
 * A wrapper over a {@link MotechEvent} with subject {@value EventSubjects#CHANNEL_REGISTER_SUBJECT}. Raised when a channel is registered with the tasks module
 */
public class ChannelRegisterEvent {

    private MotechEvent motechEvent;

    public ChannelRegisterEvent(String moduleName) {
        motechEvent = new MotechEvent(EventSubjects.CHANNEL_REGISTER_SUBJECT, new HashMap<String, Object>());
        motechEvent.getParameters().put(EventDataKeys.CHANNEL_MODULE_NAME, moduleName);
    }

    public ChannelRegisterEvent(MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
    }

    public String getChannelModuleName() {
        return (String) motechEvent.getParameters().get(EventDataKeys.CHANNEL_MODULE_NAME);
    }

    public MotechEvent toMotechEvent() {
        return motechEvent;
    }
}
