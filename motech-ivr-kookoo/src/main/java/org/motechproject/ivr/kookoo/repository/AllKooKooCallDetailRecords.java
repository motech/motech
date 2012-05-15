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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class AllKooKooCallDetailRecords extends MotechBaseRepository<KookooCallDetailRecord> {

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

    public void removeInRange(DateTime start, DateTime end) {
        removeInRange(start, end, 1);
    }

    public void removeInRange(DateTime start, DateTime end, int batchSize) {
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
}
