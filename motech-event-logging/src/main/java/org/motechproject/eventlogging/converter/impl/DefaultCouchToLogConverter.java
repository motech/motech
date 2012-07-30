package org.motechproject.eventlogging.converter.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.motechproject.eventlogging.converter.EventToLogConverter;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.domain.CouchLogMappings;
import org.motechproject.eventlogging.domain.CouchLoggableEvent;
import org.motechproject.eventlogging.domain.KeyValue;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.stereotype.Component;

@Component
public class DefaultCouchToLogConverter implements EventToLogConverter<CouchEventLog> {

    @Override
    public CouchEventLog convertToLog(MotechEvent eventToLog) {

        CouchEventLog couchEventLog = new CouchEventLog(eventToLog.getSubject(), eventToLog.getParameters(),
                DateTime.now());

        return couchEventLog;
    }

    public CouchEventLog configuredConvertEventToCouchLog(MotechEvent eventToLog, LoggableEvent loggableEvent) {
        CouchLoggableEvent couchLoggableEvent = (CouchLoggableEvent) loggableEvent;

        CouchLogMappings mappings = couchLoggableEvent.getMappings();

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

        CouchEventLog couchEventLog = new CouchEventLog(eventToLog.getSubject(), finalParameters, DateTime.now());

        return couchEventLog;
    }
}
