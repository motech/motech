package org.motechproject.ivr.kookoo.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@Repository
public class AllKooKooCallDetailRecords extends MotechBaseRepository<KookooCallDetailRecord> {

    public static final int DEFAULT_BATCH_SIZE = 1000;

    @Autowired
    public AllKooKooCallDetailRecords(@Qualifier("kookooIvrDbConnector") CouchDbConnector db) {
        super(KookooCallDetailRecord.class, db);
    }

    @View(name = "by_startDate", map = "function(doc) { \n" +
            "if (doc.type === 'KookooCallDetailRecord') \n" +
            "emit(doc.callDetailRecord.startDate, doc._id);\n" +
            "}")
    public List<KookooCallDetailRecord> findByStartDate(DateTime start, DateTime end, int limit) {
        Date startKey = start.toDateTime(DateTimeZone.UTC).toDate();
        Date endKey = end.toDateTime(DateTimeZone.UTC).toDate();
        ViewQuery query = createQuery("by_startDate").startKey(startKey).endKey(endKey).limit(limit).includeDocs(true);
        return db.queryView(query, KookooCallDetailRecord.class);
    }

    public void remove(DateTime start, DateTime end) {
        remove(start, end, DEFAULT_BATCH_SIZE);
    }

    public void remove(DateTime start, DateTime end, int batchSize) {
        DateTime startKey = start.toDateTime(DateTimeZone.UTC);
        DateTime endKey = end.toDateTime(DateTimeZone.UTC);
        while (true) {
            List<KookooCallDetailRecord> records = findByStartDate(startKey, endKey, batchSize);
            if (records.size() == 0)
                break;
            List<BulkDeleteDocument> deleteDocuments = new ArrayList<BulkDeleteDocument>();
            for (KookooCallDetailRecord record : records)
                deleteDocuments.add(BulkDeleteDocument.of(record));
            db.executeBulk(deleteDocuments);
        }
    }

    public void purge(DateTime start, DateTime end) {
        purge(start, end, DEFAULT_BATCH_SIZE);
    }

    public void purge(DateTime start, DateTime end, int batchSize) {
        DateTime startKey = start.toDateTime(DateTimeZone.UTC);
        DateTime endKey = end.toDateTime(DateTimeZone.UTC);
        while (true) {
            List<KookooCallDetailRecord> records = findByStartDate(startKey, endKey, batchSize);
            if (records.size() == 0)
                break;
            Map<String, List<String>> purgeRequest = new HashMap<String, List<String>>();
            for (KookooCallDetailRecord record : records)
                purgeRequest.put(record.getId(), asList(new String[]{ record.getRevision() }));
            db.purge(purgeRequest);
            log.info(format("purged kookoo %d call logs between %s and %s", records.size(), records.get(0).getCallDetailRecord().getStartDate(), records.get(records.size() - 1).getCallDetailRecord().getStartDate()));
        }
    }
}
