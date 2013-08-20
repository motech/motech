package org.motechproject.email.repository;

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
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailRecordSearchCriteria;
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

/**
* The <code>AllEmailRecords</code> class provides methods, allowing to work with, alter
* and search {@Link EmailRecord} documents in CouchDB
*/

@Repository
public final class AllEmailRecords extends CouchDbRepositorySupportWithLucene<EmailRecord> {

    private final Logger logger = LoggerFactory.getLogger(AllEmailRecords.class);

    public EmailRecord findLatestBy(String toAddress) {
        List<EmailRecord> emailRecords = findAllBy(new EmailRecordSearchCriteria()
                .withToAddress(toAddress));
        return CollectionUtils.isEmpty(emailRecords) ? null : (EmailRecord) sort(emailRecords, on(EmailRecord.class).getDeliveryTime(), reverseOrder()).get(0);
    }

    @FullText({@Index(
            name = "search",
            index = "function(doc) { " +
                    "var result=new Document(); " +
                    "result.add(doc.fromAddress, {'field':'fromAddress'});" +
                    "result.add(doc.toAddress, {'field':'toAddress'}); " +
                    "result.add(doc.subject, {'field':'subject'}); " +
                    "result.add(doc.message, {'field':'message'}); " +
                    "result.add(doc.deliveryTime,{'field':'deliveryTime', 'type':'date'}); " +
                    "result.add(doc.deliveryStatus, {'field':'deliveryStatus'}); " +
                    "return result " +
                    "}"
    )})
    public List<EmailRecord> findAllBy(EmailRecordSearchCriteria criteria) {
        StringBuilder query = new CouchDbLuceneQuery()
                .with("fromAddress", criteria.getFromAddress())
                .with("toAddress", criteria.getToAddress())
                .with("subject", criteria.getSubject())
                .with("message", criteria.getMessage())
                .withDateRange("deliveryTime", criteria.getDeliveryTimeRange())
                .withAny("deliveryStatus", criteria.getDeliveryStatuses())
                .build();
        return runQuery(query, criteria.getQueryParam());
    }

    private List<EmailRecord> runQuery(StringBuilder queryString, QueryParam queryParam) {
        LuceneQuery query = new LuceneQuery("EmailRecord", "search");
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
            Class clazz = EmailRecord.class;
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
        TypeReference<CustomLuceneResult<EmailRecord>> typeRef
                = new TypeReference<CustomLuceneResult<EmailRecord>>() {
        };
        return convertToEmailRecordList(db.queryLucene(query, typeRef));
    }

    private List<EmailRecord> convertToEmailRecordList(CustomLuceneResult<EmailRecord> result) {
        List<EmailRecord> emailRecords = new ArrayList<>();
        if (result != null) {
            List<CustomLuceneResult.Row<EmailRecord>> rows = result.getRows();
            for (CustomLuceneResult.Row<EmailRecord> row : rows) {
                emailRecords.add(row.getDoc());
            }
        }
        return emailRecords;
    }

    public void removeAll() {
        for (EmailRecord emailRecord : getAll()) {
            remove(emailRecord);
        }
    }

    @Autowired
    private AllEmailRecords(@Qualifier("emailDBConnector") CouchDbConnector db) throws IOException {
        super(EmailRecord.class, new LuceneAwareCouchDbConnector(db.getDatabaseName(), new StdCouchDbInstance(db.getConnection())));
        initStandardDesignDocument();
    }
}