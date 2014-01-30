package org.motechproject.scheduler.domain;

/**
 * JobBasicInfo is the class which contains information about scheduled job and its current state.
 */

public class JobBasicInfo {
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_BLOCKED = "BLOCKED";
    public static final String STATUS_PAUSED = "PAUSED";
    public static final String STATUS_OK = "OK";
    public static final String ACTIVITY_NOTSTARTED = "NOTSTARTED";
    public static final String ACTIVITY_ACTIVE = "ACTIVE";
    public static final String ACTIVITY_FINISHED = "FINISHED";
    public static final String JOBTYPE_RUNONCE = "Run Once";
    public static final String JOBTYPE_REPEATING = "Repeating";
    public static final String JOBTYPE_CRON = "Cron";

    private String activity;
    private String status;
    private String name;
    private String startDate;
    private String nextFireDate;
    private String endDate;
    private String jobType;
    private String info;

    public JobBasicInfo() {

    }

    public JobBasicInfo(String activity, String status, String name, // NO CHECKSTYLE More than 7 parameters (found 8).
                        String startDate, String nextFireDate, String endDate, String jobType, String info) {
        this.activity = activity;
        this.status = status;
        this.name = name;
        this.startDate = startDate;
        this.nextFireDate = nextFireDate;
        this.endDate = endDate;
        this.jobType = jobType;
        this.info = info;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getNextFireDate() {
        return nextFireDate;
    }

    public void setNextFireDate(String nextFireDate) {
        this.nextFireDate = nextFireDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
