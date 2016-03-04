package org.motechproject.mds.repository.query;

import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryUtil;
import org.motechproject.mds.util.InstanceSecurityRestriction;

import javax.jdo.Query;

/**
 * The <code>DataSourceReferenceQueryExecutionHelper</code> is a helper class that creates
 * <code>DataSource</code> table queries.
 */
public final class DataSourceReferenceQueryExecutionHelper {
    public static final String DATA_SOURCE_CLASS_NAME = "org.motechproject.tasks.domain.DataSource";
    public static final String TYPE_COLUMN = "type";
    public static final String NAME_COLUMN = "name";
    public static final String LOOKUP_REFERENCE_FILTER_FORMAT = TYPE_COLUMN + " == '%s' && " + NAME_COLUMN + " == '%s'";

    private DataSourceReferenceQueryExecutionHelper() {
        // dummy constructor
    }

    public static QueryExecution<Long> createLookupReferenceQuery(final String lookupName, final String entityName) {
        return new QueryExecution<Long>() {
            @Override
            public Long execute(Query query, InstanceSecurityRestriction restriction) {
                query.setFilter(String.format(LOOKUP_REFERENCE_FILTER_FORMAT, entityName, lookupName));
                QueryUtil.setCountResult(query);
                return (Long) query.execute();
            }
        };
    }
}
