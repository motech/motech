package org.motechproject.admin.listener;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.admin.events.EventKeys;
import org.motechproject.admin.events.EventSubjects;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Event handler responsible for posting {@link org.motechproject.admin.domain.StatusMessage}s. Instead of interacting
 * with the {@link StatusMessageService} to post status messages, modules can use Motech events. This handler is
 * responsible for retrieving the events and posting status messages created from the event payload.
 *
 * @see StatusMessageService
 */
@Component
public class MessageHandler {

    @Autowired
    private StatusMessageService statusMessageService;

    /**
     * Posts a status message using the {@link StatusMessageService}. The message is built from the event payload.
     * @param event the received event.
     */
    @MotechListener(subjects = EventSubjects.MESSAGE_SUBJECT)
    public void messageReceived(MotechEvent event) {
        String message = (String) event.getParameters().get(EventKeys.MESSAGE);

        String moduleName = (String) event.getParameters().get(EventKeys.MODULE_NAME);

        String levelStr = (String) event.getParameters().get(EventKeys.LEVEL);
        Level level = (StringUtils.isNotBlank(levelStr)) ? Level.valueOf(StringUtils.upperCase(levelStr)) : Level.INFO;

        DateTime timeout = (DateTime) event.getParameters().get(EventKeys.TIMEOUT);

        if (timeout == null) {
            statusMessageService.postMessage(message, moduleName, level);
        } else {
            statusMessageService.postMessage(message, moduleName, level, timeout);
        }
    }
}
