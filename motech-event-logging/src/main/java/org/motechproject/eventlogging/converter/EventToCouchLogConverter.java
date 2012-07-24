package org.motechproject.eventlogging.converter;

import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.scheduler.domain.MotechEvent;

public interface EventToCouchLogConverter {

    CouchEventLog convertEventToCouchLog(MotechEvent eventToLog);

}
