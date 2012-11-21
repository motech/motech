package org.motechproject.decisiontree.server.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.decisiontree.server.domain.CallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallDetailRecords extends MotechBaseRepository<CallDetailRecord> {

    @Autowired
    protected AllCallDetailRecords(@Qualifier("callDetailRecord") CouchDbConnector db) {
        super(CallDetailRecord.class, db);
    }

    @View(name = "by_call_id", map = "function(doc) { emit(doc.callId); }")
    public CallDetailRecord findByCallId(String callId) {
        return singleResult(queryView("by_call_id", callId));
    }

    public CallDetailRecord findOrCreate(String callId, String phoneNumber) {
        CallDetailRecord callDetailRecord = findByCallId(callId);
        if (callDetailRecord == null) {
            callDetailRecord = new CallDetailRecord(callId, phoneNumber);
            add(callDetailRecord);
        }
        return callDetailRecord;
    }
}
