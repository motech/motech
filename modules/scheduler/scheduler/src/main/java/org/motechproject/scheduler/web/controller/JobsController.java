package org.motechproject.scheduler.web.controller;

import org.motechproject.scheduler.constants.SchedulerConstants;
import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobDetailedInfo;
import org.motechproject.scheduler.contract.JobsSearchSettings;
import org.motechproject.scheduler.contract.SchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.web.domain.JobsRecords;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
        if (jobsSearchSettings.getRows() == null) {
            int defaultRowsNumber = 10;
            jobsSearchSettings.setRows(defaultRowsNumber);
        }
        if (jobsSearchSettings.getPage() == null) {
            int defaultPage = 1;
            jobsSearchSettings.setPage(defaultPage);
        }
        previousJobsRecords = new JobsRecords(
            jobsSearchSettings.getPage(), jobsSearchSettings.getRows(), rowCount, jobs
        );

        return previousJobsRecords;
    }

    /**
     * Returns detailed information about job with given ID.
     *
     * @param jobInfo  the basic information about a job
     * @return retailed information about job, null if {@code jobid} was null
     */
    @RequestMapping(value = "/job/details", method = RequestMethod.GET)
    @ResponseBody
    public JobDetailedInfo retrieveJobDetailedInfo(JobBasicInfo jobInfo) throws SchedulerException {
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

    /**
     * Schedules the given job.
     *
     * @param job  the job to be scheduled
     */
    @RequestMapping(value = "/jobs/new", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createJob(@RequestBody SchedulableJob job) {
        motechSchedulerService.scheduleJob(job);
    }

    /**
     * Edit job with the same job key as the given job.
     *
     * @param job  the updated job
     */
    @RequestMapping(value = "/jobs/edit", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void editJob(@RequestBody SchedulableJob job) {
        motechSchedulerService.updateJob(job);
    }

    /**
     * Return a job based on the given information.
     *
     * @param jobInfo  the information about a job
     * @return the job matching the information
     */
    @RequestMapping(value = "/job", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SchedulableJob getJob(JobBasicInfo jobInfo) {
        return motechSchedulerService.getJob(jobInfo);
    }

    @ExceptionHandler(MotechSchedulerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Message handleException(MotechSchedulerException e) {
        return new Message(e);
    }

    /**
     * Responsible for passing the error message key along with its parameters to be displayed on the UI.
     */
    public class Message {

        private String key;

        private List<String> params;

        public Message(MotechSchedulerException e) {
            key = e.getMessageKey();
            params = e.getParams();
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }
    }
}
