package org.motechproject.outbox.api.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.outbox.api.domain.MessageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllMessageRecords extends MotechBaseRepository<MessageRecord> {

    @Autowired
    protected AllMessageRecords(@Qualifier("outboxDatabase") CouchDbConnector db) {
        super(MessageRecord.class, db);
    }

    @View(name = "by_externalId", map = "function(doc) { if (doc.type ==='MessageRecord') { emit(doc.externalId, doc._id); }}")
    public MessageRecord getMessageRecordByExternalId(String externalId) {
        if (externalId == null) { return null; }

        ViewQuery viewQuery = createQuery("by_externalId").key(externalId).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MessageRecord.class));
    }

    public void addOrUpdateMessageRecord(MessageRecord record) {
        MessageRecord messageRecord = getMessageRecordByExternalId(record.getExternalId());
        if (messageRecord == null) {
            add(record);
        } else {
            messageRecord.setExternalId(record.getExternalId());
            messageRecord.setJobId(record.getJobId());
            update(messageRecord);
        }
    }
}
