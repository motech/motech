package org.motechproject.eventlogging.converter.impl;

import java.util.Map;
import org.joda.time.DateTime;
import org.motechproject.eventlogging.converter.EventToLogConverter;
import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.stereotype.Component;

@Component
public class DefaultFileToLogConverter implements EventToLogConverter<String> {

    @Override
    public String convertToLog(MotechEvent event) {
        StringBuilder log = new StringBuilder("EVENT: ");

        log.append(event.getSubject() + " at TIME: " + DateTime.now());

        Map<String, Object> parameters = event.getParameters();

        if (parameters.size() > 0) {
            log.append(" with PARAMETERS: ");

            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                log.append(entry.getKey() + "/" + entry.getValue().toString() + " ");
            }
        }

        return log.toString();
    }
}
