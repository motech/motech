package org.motechproject.eventlogging.repository;

import java.util.List;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCouchLogs extends MotechBaseRepository<CouchEventLog> {

    @Autowired
    protected AllCouchLogs(@Qualifier("eventLoggingDBConnector") CouchDbConnector db) {
        super(CouchEventLog.class, db);
    }

    public void log(CouchEventLog couchLog) {
        add(couchLog);
    }

    @View(name = "find_by_subject", map = "function(doc) {if(doc.type === 'CouchEventLog') { emit(doc.subject)} }")
    public List<CouchEventLog> findAllBySubject(String subject) {
        List<CouchEventLog> logs = queryView("find_by_subject", subject);
        return logs;
    }

    private static final String FUNCTION_DOC_EMIT_DOC_PARAMETERS = "function(doc) {\n"
            + "if(doc.type === 'CouchEventLog') for (var prop in doc.parameters)"
            + "    emit([prop, doc.parameters[prop]], doc._id);\n" + "}";

    @View(name = "find_by_parameter", map = FUNCTION_DOC_EMIT_DOC_PARAMETERS)
    public List<CouchEventLog> findAllByParameter(String parameter, String value) {
        List<CouchEventLog> logs = queryView("find_by_parameter", ComplexKey.of(parameter, value));
        return logs;
    }

    private static final String FUNCTION_DOC_EMIT_DOC_SUBJECT_PARAMETERS = "function(doc) { if(doc.type === \'CouchEventLog\') for (var prop in doc.parameters) emit([doc.subject, prop, doc.parameters[prop]], doc._id);}";

    @View(name = "find_by_subject_and_parameter", map = FUNCTION_DOC_EMIT_DOC_SUBJECT_PARAMETERS)
    public List<CouchEventLog> findAllBySubjectAndParameter(String subject, String parameter, String value) {
        List<CouchEventLog> logs = queryView("find_by_subject_and_parameter", ComplexKey.of(subject, parameter, value));
        return logs;
    }

}
