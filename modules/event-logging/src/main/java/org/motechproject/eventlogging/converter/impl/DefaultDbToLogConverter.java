package org.motechproject.eventlogging.converter.impl;

import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.eventlogging.converter.EventToLogConverter;
import org.motechproject.eventlogging.domain.LogMappings;
import org.motechproject.eventlogging.domain.DbLoggableEvent;
import org.motechproject.eventlogging.domain.EventLog;
import org.motechproject.eventlogging.domain.KeyValue;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementaton of the {@link EventToLogConverter} interface.
 * This class is responsible for converting {@link MotechEvent}s to the
 * class persistable in the database.
 */
@Component
public class DefaultDbToLogConverter implements EventToLogConverter<EventLog> {

    @Override
    public EventLog convertToLog(MotechEvent eventToLog) {

        EventLog eventLog = new EventLog(eventToLog.getSubject(), eventToLog.getParameters(),
                DateTime.now());

        return eventLog;
    }

    public EventLog configuredConvertEventToDbLog(MotechEvent eventToLog, LoggableEvent loggableEvent) {
        if (!(loggableEvent instanceof DbLoggableEvent)) {
            throw new MotechException("Error: unexpected loggable event type " + loggableEvent.getClass().getName());
        }
        DbLoggableEvent dbLoggableEvent = (DbLoggableEvent) loggableEvent;

        LogMappings mappings = dbLoggableEvent.getMappings();

        List<KeyValue> keyValueList = mappings.getMappings();

        List<String> exclusions = mappings.getExclusions();

        List<String> inclusions = mappings.getInclusions();

        Map<String, Object> initialParameters = eventToLog.getParameters();

        Map<String, Object> finalParameters = new LinkedHashMap<String, Object>(initialParameters);

        for (KeyValue keyValue : keyValueList) {
            if (initialParameters.containsKey(keyValue.getStartKey())) {
                if (keyValue.getStartValue().equals(initialParameters.get(keyValue.getStartKey()))) {
                    finalParameters.put(keyValue.getEndKey(), keyValue.getEndValue());
                    exclusions.add(keyValue.getStartKey());
                }
            }
        }

        for (String excludeParameter : exclusions) {
            if (initialParameters.containsKey(excludeParameter)) {
                finalParameters.remove(excludeParameter);
            }
        }

        for (String includeParameter : inclusions) {
            if (initialParameters.containsKey(includeParameter)) {
                finalParameters.put(includeParameter, initialParameters.get(includeParameter));
            }
        }

        EventLog couchEventLog = new EventLog(eventToLog.getSubject(), finalParameters, DateTime.now());

        return couchEventLog;
    }
}
