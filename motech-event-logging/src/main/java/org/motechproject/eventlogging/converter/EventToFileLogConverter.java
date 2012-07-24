package org.motechproject.eventlogging.converter;

import org.motechproject.scheduler.domain.MotechEvent;

public interface EventToFileLogConverter {

    String convertEventToLogString(MotechEvent event);

}
