package org.motechproject.scheduler.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.sql.util.Drivers;
import org.motechproject.scheduler.constants.SchedulerConstants;
import org.motechproject.scheduler.contract.EventInfo;
import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobDetailedInfo;
import org.motechproject.scheduler.contract.JobsSearchSettings;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodJobId;
import org.motechproject.scheduler.contract.RunOnceJobId;
import org.motechproject.scheduler.exception.MotechSchedulerJobRetrievalException;
import org.motechproject.scheduler.factory.MotechSchedulerFactoryBean;
import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Motech Scheduler Database Service implementation
 *
 * @see MotechSchedulerDatabaseService
 */
@Service("schedulerDatabaseService")
public class MotechSchedulerDatabaseServiceImpl implements MotechSchedulerDatabaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechSchedulerDatabaseServiceImpl.class);
    private static final String DATE_FORMAT_PATTERN = "Y-MM-dd HH:mm:ss";
    private static final String DATA_SOURCE = "org.quartz.jobStore.dataSource";
    private static final String TABLE_PREFIX = "org.quartz.jobStore.tablePrefix";
    private static final String UI_DEFINED = "uiDefined";
    private static final String START_TIME = "START_TIME";
    private static final String END_TIME = "END_TIME";
    private static final String TRIGGER_NAME = "TRIGGER_NAME";
    private static final String TRIGGER_GROUP = "TRIGGER_GROUP";
    private static final String TRIGGER_STATE = "TRIGGER_STATE";
    private static final String TRIGGER_TYPE = "TRIGGER_TYPE";
    private static final String WAITING = "WAITING";
    private static final String TRIGGERS = "TRIGGERS";
    private static final String JOB_DETAILS = "JOB_DETAILS";
    private static final String JOB_DATA = "JOB_DATA";
    private static final String OR = " OR ";
    private static final String AND = " AND ";

    @Autowired
    private Properties sqlProperties;

    private Scheduler scheduler;

    @Autowired
    private MotechSchedulerFactoryBean motechSchedulerFactoryBean;

    @PostConstruct
    public void init() {
        scheduler = motechSchedulerFactoryBean.getQuartzScheduler();
    }

    @Override
    public int countJobs(JobsSearchSettings jobsSearchSettings) throws MotechSchedulerJobRetrievalException {
        String query = buildJobsCountSqlQuery(jobsSearchSettings);
        int rowCount;
        try {
            rowCount = executeCountQuery(query);
            LOGGER.debug("Executing {}", query);
            return rowCount;
        } catch (SQLException e) {
            throw new MotechSchedulerJobRetrievalException("Jobs counting failed.", e);
        }
    }

    private String getQuery(JobsSearchSettings jobsSearchSettings){
        String query;
        if (isBlank(jobsSearchSettings.getName()) &&  isBlank(jobsSearchSettings.getActivity()) && isBlank(jobsSearchSettings.getStatus()) && isBlank(jobsSearchSettings.getTimeFrom()) && isBlank(jobsSearchSettings.getTimeTo())) { //NO CHECKSTYLE BooleanExpressionComplexity
            query = buildJobsAllSqlQuery();
        } else if (isBlank(jobsSearchSettings.getActivity()) || isBlank(jobsSearchSettings.getStatus())) {
            query = null;
        } else {
            query = buildJobsBasicInfoSqlQuery(jobsSearchSettings);
        }
        return query;
    }
    
    @Override
    public List<JobBasicInfo> getScheduledJobsBasicInfo(JobsSearchSettings jobsSearchSettings) throws MotechSchedulerJobRetrievalException {
        List<JobBasicInfo> jobBasicInfos = new LinkedList<>();
        String query = getQuery(jobsSearchSettings);
        if(query == null) {
            return jobBasicInfos;
        }

        LOGGER.debug("Executing {}", query);

        List<String> columnNames = new LinkedList<>();
        columnNames.add(TRIGGER_NAME);
        columnNames.add(TRIGGER_GROUP);
        columnNames.add(JOB_DATA);
        List<List<Object>> objects;

        try {
            objects = executeQuery(query, columnNames);

            for (List<Object> row : objects) {
                JobKey jobKey = new JobKey(row.get(0).toString(), row.get(1).toString());
                Trigger trigger = scheduler.getTriggersOfJob(jobKey).get(0);
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                String jobType = getJobType(jobKey);
                String activity = getJobActivity(trigger);
                String info = getJobInfo(trigger, jobType);
                String status = getJobStatus(trigger.getKey());
                String startDate = getStartDate(trigger);
                String nextFireDate = "";
                if (trigger.getNextFireTime() != null) {
                    nextFireDate = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN).print(trigger.getNextFireTime().getTime());
                }
                String endDate = getEndDate(trigger, jobType);
                boolean uiDefined = getUiDefined((byte[]) row.get(2));

                jobBasicInfos.add(new JobBasicInfo(
                        activity,
                        status,
                        jobName,
                        jobGroup,
                        startDate,
                        nextFireDate,
                        endDate,
                        jobType,
                        info,
                        uiDefined
                ));
            }

            return jobBasicInfos;
        } catch (SQLException | SchedulerException | ClassNotFoundException | IOException e) {
            throw new MotechSchedulerJobRetrievalException("Retrieval of scheduled jobs failed.", e);
        }
    }

    @Override
    public JobDetailedInfo getScheduledJobDetailedInfo(JobBasicInfo jobBasicInfo) throws MotechSchedulerJobRetrievalException {
        JobDetailedInfo jobDetailedInfo = new JobDetailedInfo();
        List<EventInfo> eventInfos = new ArrayList<>();

        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    if (jobKey.getName().equals(jobBasicInfo.getName())) {
                        EventInfo eventInfo = new EventInfo();
                        String subject;

                        Map<String, Object> parameters = scheduler.getJobDetail(jobKey).getJobDataMap().getWrappedMap();
                        Map<String, Object> metadata = (HashMap) parameters.get(SchedulerConstants.EVENT_METADATA);
                        parameters.remove(SchedulerConstants.EVENT_METADATA);
                        parameters.putAll(metadata);

                        eventInfo.setParameters(parameters);

                        if (eventInfo.getParameters().containsKey(SchedulerConstants.EVENT_TYPE_KEY_NAME)) {
                            subject = eventInfo.getParameters().get(SchedulerConstants.EVENT_TYPE_KEY_NAME).toString();
                            eventInfo.getParameters().remove(SchedulerConstants.EVENT_TYPE_KEY_NAME);
                        } else {
                            subject = jobKey.getName().substring(0, jobKey.getName().indexOf('-'));
                        }

                        eventInfo.setSubject(subject);
                        eventInfos.add(eventInfo);
                    }
                }
            }

            jobDetailedInfo.setEventInfoList(eventInfos);
            return jobDetailedInfo;
        } catch (SchedulerException e) {
            throw new MotechSchedulerJobRetrievalException("Retrieval of detailed info for job " + jobBasicInfo.getName() + " failed.", e);
        }
    }

    private List<List<Object>> executeQuery(String query, List<String> columns) throws SQLException {
        List<List<Object>> rows = new LinkedList<>();

        try (Connection conn = DBConnectionManager.getInstance().getConnection(sqlProperties.getProperty(DATA_SOURCE));
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                List<Object> row = new LinkedList<>();
                if (columns != null) {
                    for (String name : columns) {
                        row.add(rs.getObject(name));
                    }
                }
                rows.add(row);
            }
        }
        return rows;
    }

    private int executeCountQuery(String query) throws SQLException {
        int rowConut = 0;
        try (Connection conn = DBConnectionManager.getInstance().getConnection(sqlProperties.getProperty(DATA_SOURCE));
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            rowConut = rs.getInt(1);
        }
        return rowConut;
    }

    private void checkAndAddElement(StringBuilder sb, String element, boolean condition) {
        if (condition) {
            sb.append(element);
        }
    }

    private String buildDateRangeFilter(JobsSearchSettings jobsSearchSettings) {
        StringBuilder dateRangeSb = new StringBuilder();
        boolean addAnd = false;
        DateTime dateFrom;
        DateTime dateTo;
        if (StringUtils.isNotBlank(jobsSearchSettings.getTimeFrom())) {
            dateFrom = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN)
                    .parseDateTime(jobsSearchSettings.getTimeFrom());
            dateRangeSb.append(getCorrectNameRepresentation(START_TIME)).append(" >= ").append(dateFrom.getMillis());
            addAnd = true;
        }

        if (StringUtils.isNotBlank(jobsSearchSettings.getTimeTo())) {
            dateTo = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN)
                    .parseDateTime(jobsSearchSettings.getTimeTo());
            checkAndAddElement(dateRangeSb, AND, addAnd);
            dateRangeSb.append(getCorrectNameRepresentation(START_TIME)).append(" <= ").append(dateTo.getMillis());
        }
        return dateRangeSb.toString();
    }

    private String buildActivityFilter(JobsSearchSettings jobsSearchSettings) {
        StringBuilder activitySb = new StringBuilder();
        if (jobsSearchSettings.getActivity() != null) {
            String[] activityElements = jobsSearchSettings.getActivity().split(",");
            boolean addOr = false;
            if (activityElements.length < 3) {
                for (String element : activityElements) {
                    checkAndAddElement(activitySb, OR, addOr);
                    if (JobBasicInfo.ACTIVITY_NOTSTARTED.equals(element)) {
                        activitySb.append(getCorrectNameRepresentation(START_TIME)).append(" > ").append(DateTime.now().getMillis());
                    } else if (JobBasicInfo.ACTIVITY_FINISHED.equals(element)) {
                        activitySb.append(getCorrectNameRepresentation(END_TIME)).append(" < ").append(DateTime.now().getMillis())
                                .append(AND).append(getCorrectNameRepresentation(END_TIME)).append(" != 0");
                    } else {
                        activitySb.append(" (").append(getCorrectNameRepresentation(START_TIME)).append(" <= ")
                                .append(DateTime.now().getMillis()).append(" AND (")
                                .append(getCorrectNameRepresentation(END_TIME)).append(" >= ")
                                .append(DateTime.now().getMillis()).append(OR)
                                .append(getCorrectNameRepresentation(END_TIME)).append(" = 0))");
                    }
                    addOr = true;
                }
            }
        }
        return activitySb.toString();
    }

    private String buildStatusFilter(JobsSearchSettings jobsSearchSettings) {
        StringBuilder statusSb = new StringBuilder();
        if (jobsSearchSettings.getStatus() != null) {
            String[] statusElements = jobsSearchSettings.getStatus().split(",");
            boolean addOr = false;
            if (statusElements.length < 4) {
                for (String element : statusElements) {
                    checkAndAddElement(statusSb, OR, addOr);
                    statusSb.append(getCorrectNameRepresentation(TRIGGER_STATE)).append(" = ");
                    if (Trigger.TriggerState.ERROR.toString().equals(element)) {
                        statusSb.append("\'").append(Trigger.TriggerState.ERROR.toString()).append("\'");
                    } else if (Trigger.TriggerState.BLOCKED.toString().equals(element)) {
                        statusSb.append("\'").append(Trigger.TriggerState.BLOCKED.toString()).append("\'");
                    } else if (Trigger.TriggerState.PAUSED.toString().equals(element)) {
                        statusSb.append("\'").append(Trigger.TriggerState.PAUSED.toString()).append("\'");
                    } else {
                        statusSb.append("\'").append(Trigger.TriggerState.NORMAL.toString()).append("\'")
                            .append(OR).append(getCorrectNameRepresentation(TRIGGER_STATE)).append(" = ")
                            .append("\'").append(Trigger.TriggerState.COMPLETE.toString()).append("\'")
                            .append(OR).append(getCorrectNameRepresentation(TRIGGER_STATE)).append(" = ")
                            .append("\'").append(WAITING).append("\'");
                    }
                    addOr = true;
                }
            }
        }
        return statusSb.toString();
    }

    private List<String> buildFilters(JobsSearchSettings jobsSearchSettings) {
        List<String> filters = new ArrayList<>();
        String dateRangeFilter = buildDateRangeFilter(jobsSearchSettings);
        if (isNotBlank(dateRangeFilter)) {
            filters.add(dateRangeFilter);
        }
        String activityFilter = buildActivityFilter(jobsSearchSettings);
        if (isNotBlank(activityFilter)) {
            filters.add(activityFilter);
        }
        String statusFilter = buildStatusFilter(jobsSearchSettings);
        if (isNotBlank(statusFilter)) {
            filters.add(statusFilter);
        }
        StringBuilder nameSb = new StringBuilder();
        if (isNotBlank(jobsSearchSettings.getName())) {
            nameSb.append(getCorrectNameRepresentation(TRIGGER_NAME)).append(" LIKE ").append("\'%")
                    .append(jobsSearchSettings.getName()).append("%\'");
            filters.add(nameSb.toString());
        }

        return filters;
    }

    private String getCorrectNameRepresentation(String name) {
        return sqlProperties.get("org.quartz.dataSource.motechDS.driver").equals(Drivers.MYSQL_DRIVER) ? name : "\"" + name.toLowerCase() + "\"";
    }

    private String buildWhereCondition(JobsSearchSettings jobsSearchSettings) {
        List<String> filters = buildFilters(jobsSearchSettings);

        StringBuilder sb = new StringBuilder();
        boolean addAnd = false;
        if (filters.size() > 0) {
            sb.append(" WHERE ");
        }
        for (String filter : filters) {
            if (filter.length() > 0) {
                checkAndAddElement(sb, AND, addAnd);
                sb.append("(").append(filter).append(")");
                addAnd = true;
            }
        }
        return sb.toString();
    }

    private String buildJobsBasicInfoSqlQuery(JobsSearchSettings jobsSearchSettings) {

        StringBuilder sb = new StringBuilder("SELECT A.TRIGGER_NAME, A.TRIGGER_GROUP, B.JOB_DATA FROM ")
                .append(getCorrectNameRepresentation(sqlProperties.get(TABLE_PREFIX).toString() + TRIGGERS))
                .append(" AS A JOIN ")
                .append(getCorrectNameRepresentation(sqlProperties.get(TABLE_PREFIX).toString() + JOB_DETAILS))
                .append(" AS B")
                .append(" ON A.TRIGGER_NAME = B.JOB_NAME AND A.TRIGGER_GROUP = B.JOB_GROUP")
                .append(buildWhereCondition(jobsSearchSettings));

        if (isNotBlank(jobsSearchSettings.getSortColumn()) && isNotBlank(jobsSearchSettings.getSortDirection())) {
            sb.append(" ORDER BY ")
                    .append("A.")
                    .append(getCorrectNameRepresentation(getSortColumn(jobsSearchSettings.getSortColumn())))
                    .append(" ")
                    .append(jobsSearchSettings.getSortDirection().toUpperCase());
        }
        if (jobsSearchSettings.getRows() != null && jobsSearchSettings.getPage() != null) {
            int offset = (jobsSearchSettings.getPage() == 0) ? 0 : (jobsSearchSettings.getPage() - 1) * jobsSearchSettings.getRows();
            sb.append(" LIMIT ").append(jobsSearchSettings.getRows()).append(" OFFSET ").append(offset);
        }

        return sb.toString();
    }

    private String buildJobsCountSqlQuery(JobsSearchSettings jobsSearchSettings) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ")
            .append(getCorrectNameRepresentation(sqlProperties.get(TABLE_PREFIX).toString() + TRIGGERS))
            .append(buildWhereCondition(jobsSearchSettings));
        return sb.toString();
    }

    private String buildJobsAllSqlQuery() {
        StringBuilder sb = new StringBuilder("SELECT A. TRIGGER_NAME, A.TRIGGER_GROUP, B.JOB_DATA FROM ")
            .append(getCorrectNameRepresentation(sqlProperties.get(TABLE_PREFIX).toString() + TRIGGERS))
            .append(" AS A JOIN QRTZ_JOB_DETAILS AS B ON A.TRIGGER_NAME = B.JOB_NAME AND A.TRIGGER_GROUP = B.JOB_GROUP");
        return sb.toString();
    }

    private String getSortColumn(String column) {
        String sortColumn;

        if (column.equalsIgnoreCase("startDate")) {
            sortColumn = START_TIME;
        } else if (column.equalsIgnoreCase("endDate")) {
            sortColumn = END_TIME;
        } else if (column.equalsIgnoreCase("status")) {
            sortColumn = TRIGGER_STATE;
        } else if (column.equalsIgnoreCase("jobType")) {
            sortColumn = TRIGGER_TYPE;
        } else {
            sortColumn = TRIGGER_NAME;
        }

        return sortColumn;
    }

    private String getJobInfo(Trigger trigger, String jobType) throws SchedulerException {
        if (jobType.equals(JobBasicInfo.JOBTYPE_REPEATING)) {
            Integer timesTriggered = 0;
            String repeatMaxCount = "-";

            if (trigger instanceof CalendarIntervalTrigger) {
                CalendarIntervalTrigger calendarIntervalTrigger = (CalendarIntervalTrigger) trigger;

                timesTriggered = calendarIntervalTrigger.getTimesTriggered();
            } else if (trigger instanceof SimpleTrigger) {
                SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;

                timesTriggered = simpleTrigger.getTimesTriggered();
            }

            if (trigger.getEndTime() != null) {
                repeatMaxCount = Integer.toString(TriggerUtils.computeFireTimesBetween(
                        (OperableTrigger) trigger, null, trigger.getStartTime(), trigger.getEndTime()
                ).size() + timesTriggered);
            }

            return String.format("%d/%s", timesTriggered, repeatMaxCount);
        } else if (jobType.equals(JobBasicInfo.JOBTYPE_CRON)) {
            CronScheduleBuilder cronScheduleBuilder = (CronScheduleBuilder) trigger.getScheduleBuilder();

            CronTrigger cronTrigger = (CronTrigger) cronScheduleBuilder.build();

            return cronTrigger.getCronExpression();
        } else {
            return "-";
        }
    }

    private String getJobType(JobKey jobKey) throws SchedulerException {
        if (jobKey.getName().endsWith(RunOnceJobId.SUFFIX_RUNONCEJOBID)) {
            return JobBasicInfo.JOBTYPE_RUNONCE;
        } else if (jobKey.getName().endsWith(RepeatingJobId.SUFFIX_REPEATJOBID)) {
            return JobBasicInfo.JOBTYPE_REPEATING;
        } else if (jobKey.getName().endsWith(RepeatingPeriodJobId.SUFFIX_REPEATPERIODJOBID)) {
            return JobBasicInfo.JOBTYPE_PERIOD;
        } else {
            return JobBasicInfo.JOBTYPE_CRON;
        }
    }

    private String getStartDate(Trigger trigger) throws SchedulerException {
        return DateTimeFormat.forPattern(DATE_FORMAT_PATTERN).print(trigger.getStartTime().getTime());
    }

    private String getEndDate(Trigger trigger, String jobType) throws SchedulerException {
        DateTime endDateTime = new DateTime(trigger.getEndTime());
        String startDate = getStartDate(trigger);
        String endDate;

        if (!endDateTime.isAfterNow()) {
            if (jobType.equals(JobBasicInfo.JOBTYPE_RUNONCE)) {
                endDate = startDate;
            } else {
                endDate = "-";
            }
        } else {
            endDate = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN).print(endDateTime);
        }

        return endDate;
    }

    private String getJobActivity(Trigger trigger) throws SchedulerException {
        DateTime startDateTime = new DateTime(trigger.getStartTime());
        DateTime endDateTime = new DateTime(trigger.getEndTime());

        if (startDateTime.isAfterNow()) {
            return JobBasicInfo.ACTIVITY_NOTSTARTED;
        } else if (endDateTime.isBeforeNow()) {
            return  JobBasicInfo.ACTIVITY_FINISHED;
        } else {
            return JobBasicInfo.ACTIVITY_ACTIVE;
        }
    }

    private String getJobStatus(TriggerKey triggerKey) throws SchedulerException {
        Trigger.TriggerState currentTriggerState = scheduler.getTriggerState(triggerKey);
        if (currentTriggerState == Trigger.TriggerState.ERROR) {
            return JobBasicInfo.STATUS_ERROR;
        } else if (currentTriggerState == Trigger.TriggerState.BLOCKED) {
            return JobBasicInfo.STATUS_BLOCKED;
        } else if (currentTriggerState == Trigger.TriggerState.PAUSED) {
            return JobBasicInfo.STATUS_PAUSED;
        } else {
            return JobBasicInfo.STATUS_OK;
        }
    }

    private boolean getUiDefined(byte[] bytes) throws IOException, ClassNotFoundException {
        try (InputStream is = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(is)) {
            JobDataMap jobDataMap = (JobDataMap) ois.readObject();
            return isUiDefined(jobDataMap);
        }
    }

    private boolean isUiDefined(JobDataMap jobDataMap) {
        return jobDataMap.get(SchedulerConstants.EVENT_METADATA) != null &&  (Boolean) ((Map<String, Object>) jobDataMap.get(SchedulerConstants.EVENT_METADATA)).get(UI_DEFINED);
    }
}