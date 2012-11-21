package org.motechproject.decisiontree.server.service.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllFlowSessionRecords extends MotechBaseRepository<FlowSessionRecord> {
    @Autowired
    protected AllFlowSessionRecords(@Qualifier("flowSession") CouchDbConnector db) {
        super(FlowSessionRecord.class, db);
    }

    @View(name = "by_session_id", map = "function(doc) { emit(doc.callDetailRecord['callId']); }")
    public FlowSessionRecord findBySessionId(String sessionId) {
        return singleResult(queryView("by_session_id", sessionId.toUpperCase()));
    }

    public FlowSessionRecord findOrCreate(String sessionId, String phoneNumber) {
        FlowSessionRecord flowSessionRecord = findBySessionId(sessionId);
        if (flowSessionRecord == null) {
            flowSessionRecord = new FlowSessionRecord(sessionId.toUpperCase(), phoneNumber);
            add(flowSessionRecord);
        }
        return flowSessionRecord;
    }
}
