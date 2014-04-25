package org.motechproject.eventlogging.service.impl;

import org.motechproject.eventlogging.domain.EventLog;
import org.motechproject.eventlogging.service.EventLogService;
import org.motechproject.eventlogging.service.EventQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The <code>DbEventQueryService</code> allows to query for records, given certain criteria.
 */
@Service
public class DbEventQueryService implements EventQueryService<EventLog> {

    @Autowired
    private EventLogService logService;

    @Override
    public List<EventLog> getAllEventsBySubject(String subject) {
        return logService.findBySubject(subject);
    }

    @Override
    public List<EventLog> getAllEventsByParameter(String parameter, String value) {
        return filterLogsByKeyValue(logService.retrieveAll(), parameter, value);
    }

    @Override
    public List<EventLog> getAllEventsBySubjectAndParameter(String subject, String parameter, String value) {
        return filterLogsByKeyValue(logService.findBySubject(subject), parameter, value);
    }

    private List<EventLog> filterLogsByKeyValue(List<EventLog> logs, String parameter, String value) {
        for (Iterator it = logs.iterator(); it.hasNext(); it.next()) {
            Map<String, Object> parameterMap = (Map) ((EventLog) it.next()).getParameters();
            if (!(parameterMap.containsKey(parameter) || value.equals(parameterMap.get(parameter)))) {
                it.remove();
            }
        }

        return logs;
    }

}
