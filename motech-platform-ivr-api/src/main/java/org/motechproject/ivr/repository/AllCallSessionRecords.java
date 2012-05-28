package org.motechproject.ivr.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallSessionRecords extends MotechBaseRepository<CallSessionRecord> {
    @Autowired
    protected AllCallSessionRecords(@Qualifier("platformIVRDbConnector") CouchDbConnector db) {
        super(CallSessionRecord.class, db);
    }

    @View(name = "by_session_id", map = "function(doc) { emit(doc.sessionId); }")
    public CallSessionRecord findBySessionId(String sessionId) {
        return singleResult(queryView("by_session_id", sessionId.toUpperCase()));
    }

    public CallSessionRecord findOrCreate(String sessionId) {
        CallSessionRecord callSessionRecord = findBySessionId(sessionId);
        if (callSessionRecord == null) {
            callSessionRecord = new CallSessionRecord(sessionId.toUpperCase());
            add(callSessionRecord);
        }
        return callSessionRecord;
    }
}
