package org.motechproject.admin.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.admin.domain.LogMapping;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllLogMappings extends MotechBaseRepository<LogMapping> {

    @Autowired
    public AllLogMappings(@Qualifier("adminDbConnector") CouchDbConnector connector) {
        super(LogMapping.class, connector);
    }

    @View(name = "by_logName", map = "function(doc) { if(doc.type === 'LogMapping') emit(doc.logName); }")
    public LogMapping byLogName(String logName) {
        List<LogMapping> result = queryView("by_logName", logName);
        return (result.isEmpty() ? null : result.get(0));
    }

    public void addOrUpdate(LogMapping mapping) {
        LogMapping existing = byLogName(mapping.getLogName());

        if (existing == null) {
            add(mapping);
        } else {
            existing.setLogName(mapping.getLogName());
            existing.setLogLevel(mapping.getLogLevel());
            update(existing);
        }
    }

    public void removeByLogName(String name) {
        LogMapping mapping = byLogName(name);

        if (mapping != null) {
            remove(mapping);
        }
    }
}
