package org.motechproject.tasks.domain;

import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.constants.EventDataKeys;
import org.motechproject.tasks.constants.EventSubjects;

import java.util.HashMap;

/**
 * A wrapper over a {@link MotechEvent} with subject {@value EventSubjects#CHANNEL_REGISTER_SUBJECT}. Raised when a
 * channel is registered with the tasks module
 */
public class ChannelRegisterEvent {

    private MotechEvent motechEvent;

    /**
     * Constructor.
     *
     * @param moduleName  the module name
     */
    public ChannelRegisterEvent(String moduleName) {
        motechEvent = new MotechEvent(EventSubjects.CHANNEL_REGISTER_SUBJECT, new HashMap<String, Object>());
        motechEvent.getParameters().put(EventDataKeys.CHANNEL_MODULE_NAME, moduleName);
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the motech event
     */
    public ChannelRegisterEvent(MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
    }

    public String getChannelModuleName() {
        return (String) motechEvent.getParameters().get(EventDataKeys.CHANNEL_MODULE_NAME);
    }

    /**
     * Convert this event to the instance of {@code MotechEvent}.
     *
     * @return the instance of {@code MotechEvent}
     */
    public MotechEvent toMotechEvent() {
        return motechEvent;
    }
}
