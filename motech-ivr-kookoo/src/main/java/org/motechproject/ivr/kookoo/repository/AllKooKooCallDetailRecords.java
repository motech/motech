package org.motechproject.ivr.kookoo.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;

public class AllKooKooCallDetailRecords extends MotechBaseRepository<KookooCallDetailRecord> {

    protected AllKooKooCallDetailRecords(Class<KookooCallDetailRecord> type, CouchDbConnector db) {
        super(type, db);
    }

    public KookooCallDetailRecord findByCallId(String callId) {
        return null;
    }
}
