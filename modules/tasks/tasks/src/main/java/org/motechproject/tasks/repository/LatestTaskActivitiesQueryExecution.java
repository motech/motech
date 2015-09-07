package org.motechproject.tasks.repository;

import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.tasks.domain.TaskActivity;

import javax.jdo.Query;
import java.util.List;

/**
 * Custom MDS query that allows the retrieval of the latest task activity
 * records from the database. The result of the query is a maximum of a
 * 10 latest records, ordered descending by date.
 */
public class LatestTaskActivitiesQueryExecution implements QueryExecution<List<TaskActivity>> {

    private static final Long START_RANGE = 0L;
    private static final Long END_RANGE = 10L;
    private static final String BY_DATE_DESCENDING = "date descending";

    /**
     * Executes MDS query that retrieves 10 latest activity records.
     *
     * @param query query to execute
     * @param restriction instance security restriction
     * @return A list of 10 most recent task activity records, ordered descending by date
     */
    @Override
    public List<TaskActivity> execute(Query query, InstanceSecurityRestriction restriction) {
        query.setClass(TaskActivity.class);
        query.setOrdering(BY_DATE_DESCENDING);
        query.setRange(START_RANGE, END_RANGE);

        return (List<TaskActivity>) query.execute();
    }
}
