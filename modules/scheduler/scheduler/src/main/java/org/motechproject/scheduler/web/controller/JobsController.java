package org.motechproject.scheduler.web.controller;

import org.motechproject.scheduler.constants.SchedulerConstants;
import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobDetailedInfo;
import org.motechproject.scheduler.contract.JobsSearchSettings;
import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.motechproject.scheduler.web.domain.JobsRecords;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.List;

/**
 * JobsController is the Spring Framework Controller, its used by view layer for getting information about
 * scheduled jobs and about event associated with given Job. The methods of controller return JobsRecords class
 * for jobs data and JobDetailedInfo class for event data. The internal methods of this class performs
 * filtering and sorting of jobs information. The class also stores the most recent JobsRecords for information which
 * job event data should be returned.
 *
 * @see JobsRecords
 * @see JobDetailedInfo
 * */

@Controller
@PreAuthorize(SchedulerConstants.VIEW_SCHEDULER_JOBS)
public class JobsController {

    @Autowired
    private MotechSchedulerDatabaseService motechSchedulerDatabaseService;

    private JobsRecords previousJobsRecords;

    /**
     * Returns job information sorted and filtered as defined in {@code jobsGridSettings}.
     *
     * @param jobsSearchSettings  the setting by which returned records are sorted and filtered
     * @return sorted and filtered job records
     */
    @RequestMapping({ "/jobs" })
    @ResponseBody
    public JobsRecords retrieveJobInfo(JobsSearchSettings jobsSearchSettings) throws SchedulerException, SQLException {
        List<JobBasicInfo> jobs = motechSchedulerDatabaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
        int rowCount = jobs.size() == 0 ? 0 : motechSchedulerDatabaseService.countJobs(jobsSearchSettings);
        previousJobsRecords = new JobsRecords(
            jobsSearchSettings.getPage(), jobsSearchSettings.getRows(), rowCount, jobs
        );

        return previousJobsRecords;
    }

    /**
     * Returns detailed information about job with given ID.
     *
     * @param jobid  the jobs ID, not null
     * @return retailed information about job, null if {@code jobid} was null
     */
    @RequestMapping({ "/jobs/{jobid}" })
    @ResponseBody
    public JobDetailedInfo retrieveJobDetailedInfo(@PathVariable int jobid) throws SchedulerException {
        if (previousJobsRecords != null) {
            return motechSchedulerDatabaseService.getScheduledJobDetailedInfo(previousJobsRecords.getRows().get(jobid - 1));
        } else {
            return null;
        }
    }
}
