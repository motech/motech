package org.motechproject.scheduler.web.controller;

import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobDetailedInfo;
import org.motechproject.scheduler.contract.JobsSearchSettings;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.web.domain.JobsRecords;
import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class JobsController {

    @Autowired
    private MotechSchedulerDatabaseService motechSchedulerDatabaseService;

    @Autowired
    private MotechSchedulerService motechSchedulerService;

    private JobsRecords previousJobsRecords;

    /**
     * Returns job information sorted and filtered as defined in {@code jobsGridSettings}.
     *
     * @param jobsSearchSettings  the setting by which returned records are sorted and filtered
     * @return sorted and filtered job records
     */
    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
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
    @RequestMapping(value = "/job/details", method = RequestMethod.POST)
    @ResponseBody
    public JobDetailedInfo retrieveJobDetailedInfo(@RequestBody JobBasicInfo jobInfo) throws SchedulerException {
        return motechSchedulerDatabaseService.getScheduledJobDetailedInfo(jobInfo);
    }

    /**
     * Pauses the job based on the given {@code jobInfo}.
     *
     * @param jobInfo  the information about a job
     * @return the updated job
     */
    @RequestMapping(value = "/job/pause", method = RequestMethod.POST)
    @ResponseBody
    public JobBasicInfo pauseJob(@RequestBody JobBasicInfo jobInfo) throws SchedulerException {
        return motechSchedulerService.pauseJob(jobInfo);
    }

    /**
     * Resumes the job based on the given {@code jobInfo}.
     *
     * @param jobInfo  the information about a job
     * @return the updated job
     */
    @RequestMapping(value = "/job/resume", method = RequestMethod.POST)
    @ResponseBody
    public JobBasicInfo resumeJob(@RequestBody JobBasicInfo jobInfo) throws SchedulerException {
        return motechSchedulerService.resumeJob(jobInfo);
    }

    /**
     * Deletes the job based on the given {@code jobInfo}.
     *
     * @param jobInfo  the information about a job
     */
    @RequestMapping(value = "/job/delete", method = RequestMethod.POST)
    @ResponseBody
    public void deleteJob(@RequestBody JobBasicInfo jobInfo) throws SchedulerException {
        motechSchedulerService.deleteJob(jobInfo);
    }
}
