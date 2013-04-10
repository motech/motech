package org.motechproject.sms.api.repository;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.CustomLuceneResult;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import org.codehaus.jackson.type.TypeReference;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.lucene.query.CouchDbLuceneQuery;
import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsRecordSearchCriteria;
import org.motechproject.sms.api.domain.SmsRecords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static java.util.Collections.reverseOrder;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Repository
public class AllSmsRecords extends CouchDbRepositorySupportWithLucene<SmsRecord> {

    private final Logger logger = LoggerFactory.getLogger(AllSmsRecords.class);

    public void updateDeliveryStatus(String recipient, String referenceNumber, String deliveryStatus) {
        SmsRecord smsRecord = findLatestBy(recipient, referenceNumber);
        if (smsRecord != null) {
            smsRecord.setStatus(DeliveryStatus.valueOf(deliveryStatus));
            update(smsRecord);
        }
    }

    SmsRecord findLatestBy(String recipient, String referenceNumber) {
        SmsRecords smsRecords = findAllBy(new SmsRecordSearchCriteria()
                .withPhoneNumber(recipient)
                .withReferenceNumber(referenceNumber));
        return CollectionUtils.isEmpty(smsRecords.getRecords()) ? null : (SmsRecord) sort(smsRecords.getRecords(), on(SmsRecord.class).getMessageTime(), reverseOrder()).get(0);
    }

    public void addOrReplace(SmsRecord smsRecord) {
        SmsRecords smsRecordsInDb = findAllBy(new SmsRecordSearchCriteria()
                .withPhoneNumber(smsRecord.getPhoneNumber())
                .withMessageTime(smsRecord.getMessageTime())
                .withReferenceNumber(smsRecord.getReferenceNumber()));
        if (CollectionUtils.isEmpty(smsRecordsInDb.getRecords())) {
            add(smsRecord);
        } else {
            SmsRecord smsRecordInDb = smsRecordsInDb.getRecords().get(0);
            smsRecord.setId(smsRecordInDb.getId());
            smsRecord.setRevision(smsRecordInDb.getRevision());
            update(smsRecord);
        }
    }

    @FullText({@Index(
            name = "search",
            index = "function(doc) { " +
                    "var result=new Document(); " +
                    "result.add(doc.smsType,{'field':'smsType'}); " +
                    "result.add(doc.phoneNumber, {'field':'phoneNumber'});" +
                    "result.add(doc.messageContent, {'field':'messageContent'}); " +
                    "result.add(doc.messageTime,{'field':'messageTime', 'type':'date'}); " +
                    "result.add(doc.deliveryStatus, {'field':'deliveryStatus'}); " +
                    "result.add(doc.referenceNumber, {'field':'referenceNumber'}); " +
                    "return result " +
                    "}"
    )})

    public SmsRecords findAllBy(SmsRecordSearchCriteria criteria) {
        StringBuilder query = new CouchDbLuceneQuery()
                .withAny("smsType", criteria.getSmsTypes())
                .with("phoneNumber", criteria.getPhoneNumber())
                .with("messageContent", criteria.getMessageContent())
                .withDateRange("messageTime", criteria.getMessageTimeRange())
                .withAny("deliveryStatus", criteria.getDeliveryStatuses())
                .with("referenceNumber", criteria.getReferenceNumber())
                .build();
        return runQuery(query, criteria.getQueryParam());
    }

    private SmsRecords runQuery(StringBuilder queryString, QueryParam queryParam) {
        LuceneQuery query = new LuceneQuery("SmsRecord", "search");
        query.setQuery(queryString.toString());
        query.setStaleOk(false);
        query.setIncludeDocs(true);
        int recordsPerPage = queryParam.getRecordsPerPage();
        if (recordsPerPage > 0) {
            query.setLimit(recordsPerPage);
            query.setSkip(queryParam.getPageNumber() * recordsPerPage);
        }
        String sortBy = queryParam.getSortBy();
        if (isNotBlank(sortBy)) {
            Class clazz = SmsRecord.class;
            try {
                Field f = clazz.getDeclaredField(sortBy);
                if (f.getType().equals(DateTime.class)) {
                    sortBy = sortBy + "<date>";
                }
            } catch (NoSuchFieldException e) {
                logger.error(String.format("No found field %s", sortBy), e);
            }
            String sortString = queryParam.isReverse() ? "\\" + sortBy : sortBy;
            query.setSort(sortString);
        }
        TypeReference<CustomLuceneResult<SmsRecord>> typeRef
                = new TypeReference<CustomLuceneResult<SmsRecord>>() {
        };
        return convertToSmsRecords(db.queryLucene(query, typeRef));
    }

    private SmsRecords convertToSmsRecords(CustomLuceneResult<SmsRecord> result) {
        List<SmsRecord> smsRecords = new ArrayList<>();
        int count = 0;
        if (result != null) {
            List<CustomLuceneResult.Row<SmsRecord>> rows = result.getRows();
            for (CustomLuceneResult.Row<SmsRecord> row : rows) {
                smsRecords.add(row.getDoc());
            }
            count = result.getTotalRows();
        }
        return new SmsRecords(count, smsRecords);
    }

    //TODO: Create Base class and move it to there.
    public void removeAll() {
        for (SmsRecord smsRecord : getAll()) {
            remove(smsRecord);
        }
    }

    @Autowired
    protected AllSmsRecords(@Qualifier("smsDBConnector") CouchDbConnector db) throws IOException {
        super(SmsRecord.class, new LuceneAwareCouchDbConnector(db.getDatabaseName(), new StdCouchDbInstance(db.getConnection())));
        initStandardDesignDocument();
    }
}
