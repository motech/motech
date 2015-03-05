package org.motechproject.scheduler.web.controller;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobDetailedInfo;
import org.motechproject.scheduler.domain.JobBasicInfoComparator;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.web.domain.JobsGridSettings;
import org.motechproject.scheduler.web.domain.JobsRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
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
    private MotechSchedulerService motechSchedulerService;

    private JobsRecords previousJobsRecords;

    /**
     * Returns job information sorted and filtered as defined in {@code jobsGridSettings}.
     *
     * @param jobsGridSettings  the setting by which returned records are sorted and filtered
     * @return sorted and filtered job records
     */
    @RequestMapping({ "/jobs" })
    @ResponseBody
    public JobsRecords retrieveJobInfo(JobsGridSettings jobsGridSettings) {
        List<JobBasicInfo> allJobsBasicInfos = motechSchedulerService.getScheduledJobsBasicInfo();
        List<JobBasicInfo> filteredJobsBasicInfos = null;
        Boolean sortAscending = true;
        DateTime dateFrom = null;
        DateTime dateTo = null;

        if (jobsGridSettings.getSortDirection() != null) {
            sortAscending = "asc".equals(jobsGridSettings.getSortDirection());
        }

        if (!jobsGridSettings.getTimeFrom().isEmpty()) {
            dateFrom = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss")
                    .parseDateTime(jobsGridSettings.getTimeFrom());
        }

        if (!jobsGridSettings.getTimeTo().isEmpty()) {
            dateTo = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss")
                    .parseDateTime(jobsGridSettings.getTimeTo());
        }

        filteredJobsBasicInfos = filterJobsByDates(allJobsBasicInfos, dateFrom, dateTo);

        filteredJobsBasicInfos = filterJobsByStates(
                filteredJobsBasicInfos, jobsGridSettings.getActivity(), jobsGridSettings.getStatus()
        );

        filteredJobsBasicInfos = filterJobsByName(filteredJobsBasicInfos, jobsGridSettings.getName());

        if (!Strings.isNullOrEmpty(jobsGridSettings.getSortColumn())) {
            Collections.sort(
                    filteredJobsBasicInfos, new JobBasicInfoComparator(
                            sortAscending, jobsGridSettings.getSortColumn()
                    )
            );
        }

        previousJobsRecords = new JobsRecords(
            jobsGridSettings.getPage(), jobsGridSettings.getRows(), filteredJobsBasicInfos
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
    public JobDetailedInfo retrieveJobDetailedInfo(@PathVariable int jobid) {
        if (previousJobsRecords != null) {
            return motechSchedulerService.getScheduledJobDetailedInfo(previousJobsRecords.getRows().get(jobid - 1));
        } else {
            return null;
        }
    }

    private List<JobBasicInfo> filterJobsByDates(List<JobBasicInfo> jobs, DateTime dateFrom, DateTime dateTo) {
        List<JobBasicInfo> filteredJobs = new ArrayList<>();

        for (JobBasicInfo job : jobs) {
            DateTime jobStartTime = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss")
                    .parseDateTime(job.getStartDate());

            boolean before = dateFrom != null && jobStartTime.isBefore(dateFrom);
            boolean after = dateTo != null && jobStartTime.isAfter(dateTo);

            if (!before && !after) {
                filteredJobs.add(job);
            }
        }

        return filteredJobs;
    }

    private List<JobBasicInfo> filterJobsByStates(List<JobBasicInfo> jobs, String activityFilter, String statusFilter) {
        List<JobBasicInfo> filteredJobs = new ArrayList<>();

        for (JobBasicInfo job : jobs) {
            if (activityFilter.contains(job.getActivity()) && statusFilter.contains(job.getStatus())) {
                filteredJobs.add(job);
            }
        }

        return filteredJobs;
    }

    private List<JobBasicInfo> filterJobsByName(List<JobBasicInfo> jobs, String namePartial) {
        List<JobBasicInfo> filteredJobs = new ArrayList<>();

        for (JobBasicInfo job : jobs) {
            if (job.getName().contains(namePartial)) {
                filteredJobs.add(job);
            }
        }

        return filteredJobs;
    }
}
