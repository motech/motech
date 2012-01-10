package org.motechproject.server.voxeo.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.server.voxeo.domain.PhoneCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class AllPhoneCalls extends MotechBaseRepository<PhoneCall> {

    @Autowired
    public AllPhoneCalls(@Qualifier("voxeoDbConnector") CouchDbConnector db) {
        super(PhoneCall.class, db);
    }

    @View(name = "findBySessionId", map = "function(doc) {if (doc.type == 'CALLDETAILRECORD' && doc.sessionId) {emit(doc.sessionId, doc._id);}}")
    public PhoneCall findBySessionId(String sessionId) {
        ViewQuery q = createQuery("findBySessionId").key(sessionId).includeDocs(true);
        List<PhoneCall> detailRecords = db.queryView(q, PhoneCall.class);
        return detailRecords.isEmpty() ? null : detailRecords.get(0);
    }
}
