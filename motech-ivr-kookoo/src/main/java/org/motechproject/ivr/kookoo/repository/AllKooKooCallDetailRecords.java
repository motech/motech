package org.motechproject.ivr.kookoo.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllKooKooCallDetailRecords extends MotechBaseRepository<KookooCallDetailRecord> {

    @Autowired
    public AllKooKooCallDetailRecords(@Qualifier("kookooIvrDbConnector") CouchDbConnector db) {
        super(KookooCallDetailRecord.class, db);
        this.initStandardDesignDocument();
    }

    @View(name = "findByCallId", map = "function(doc) {if (doc.type == 'KookooCallDetailRecord') {emit(doc.callDetailRecord.callId, doc._id);}}")
    public KookooCallDetailRecord findByCallId(String callId) {
        ViewQuery q = createQuery("findByCallId").key(callId).includeDocs(true);
        List<KookooCallDetailRecord> callDetailRecords = db.queryView(q, KookooCallDetailRecord.class);
        return (callDetailRecords == null || callDetailRecords.isEmpty()) ? null : callDetailRecords.get(0);
    }
}
