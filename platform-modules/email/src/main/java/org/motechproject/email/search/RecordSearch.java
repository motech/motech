package org.motechproject.email.search;

import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.query.QueryUtil;

import javax.jdo.Query;
import java.util.List;

/**
 * Email record search implementation returning a list of records
 * matching the criteria.
 */
public class RecordSearch extends AbstractSearchExecution<List<EmailRecord>> {

    public RecordSearch(EmailRecordSearchCriteria criteria) {
        super(criteria);
    }

    @Override
    public List<EmailRecord> execute(Query query, List<Property> properties) {
        QueryUtil.setQueryParams(query, getCriteria().getQueryParams());

        return (List<EmailRecord>) QueryExecutor.executeWithArray(query, properties);
    }
}
