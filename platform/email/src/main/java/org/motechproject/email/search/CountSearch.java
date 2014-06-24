package org.motechproject.email.search;

import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.query.QueryUtil;

import javax.jdo.Query;
import java.util.List;

/**
 * Email record search implementation that returns the total record count
 * matching the criteria.
 */
public class CountSearch extends AbstractSearchExecution<Long> {

    public CountSearch(EmailRecordSearchCriteria criteria) {
        super(criteria);
    }

    @Override
    public Long execute(Query query, List<Property> properties) {
        QueryUtil.setCountResult(query);

        return (long) QueryExecutor.executeWithArray(query, properties);
    }
}
