package org.motechproject.scheduler.service;

import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobDetailedInfo;
import org.motechproject.scheduler.contract.JobsSearchSettings;
import org.motechproject.scheduler.exception.MotechSchedulerJobRetrievalException;
import org.motechproject.tasks.domain.TriggerEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *  Service provides methods used to get data from
 *  Scheduler. Also provides pagination to use with
 *  jqGrid.
 */
public interface MotechSchedulerDatabaseService {

    /**
     * Returns info about scheduled jobs for given filter information
     * Sorts all jobs with ascending or descending order for given column.
     *
     * @param jobsSearchSettings contains filter, sorting and pagination jobs options.
     *
     * @return list with {@link org.motechproject.scheduler.contract.JobBasicInfo}
     * for given sorting and pagination option
     *
     * @throws MotechSchedulerJobRetrievalException when the query fails.
     */
    List<JobBasicInfo> getScheduledJobsBasicInfo(JobsSearchSettings jobsSearchSettings) throws MotechSchedulerJobRetrievalException;

    /**
     * Returns detailed information about job matching given {@code JobBasicInfo}.
     *
     * @param jobBasicInfo  the {@code JobBasicInfo} about the job
     *
     * @return the detailed information about job
     */
    JobDetailedInfo getScheduledJobDetailedInfo(JobBasicInfo jobBasicInfo) throws MotechSchedulerJobRetrievalException;

    /**
     * Counts all triggers in TRIGGER table which matches the filters built from grid settings.
     *
     * @param jobsSearchSettings contains filter jobs information.
     *
     * @return number of all triggers which matches the filters built from grid settings.
     *
     * @throws MotechSchedulerJobRetrievalException when the query fails.
     */
    int countJobs(JobsSearchSettings jobsSearchSettings) throws MotechSchedulerJobRetrievalException;

    /**
     * Returns a list of triggers based on the given {@code page} and {@code pageSize}.
     *
     * @param page  the number of the page
     * @param pageSize  the size of the page
     * @return the list of triggers
     */
    List<TriggerEvent> getTriggers(int page, int pageSize) throws SQLException, IOException, ClassNotFoundException;

    /**
     * Returns a trigger with the given subject.
     *
     * @param subject  the subject of the trigger
     * @return the trigger with the given subject if it exists, null otherwise
     */
    TriggerEvent getTrigger(String subject) throws SQLException, IOException, ClassNotFoundException;

    /**
     * Returns the amount of triggers.
     *
     * @return the number of triggers
     */
    long countTriggers() throws SQLException;
}
