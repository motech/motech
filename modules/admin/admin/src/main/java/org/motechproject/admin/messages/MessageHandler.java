package org.motechproject.admin.messages;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.admin.events.EventKeys;
import org.motechproject.admin.events.EventSubjects;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {

    @Autowired
    private StatusMessageService statusMessageService;

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
