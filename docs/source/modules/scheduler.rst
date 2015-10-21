.. _scheduler-module:

Scheduler Module
================

Description
-----------
Scheduler module allows users to schedule, execute and browse jobs of several types.
A job is a Motech event that will fire when the job is triggered.
It uses `quartz library`_ to store scheduled jobs.
All the information can be stored either in RAM job store or in a database, which can be any database supported by quartz (though we recommend using MySQL or Postgresql).
Using RAM storage is not recommended in production because of possibility of running out of memory and lack of jobs persistence, which can lead to data loss.
Module comes with scripts stored in :code:`/sql` sub-folder, which can be used to create database structure for quartz.

You can schedule many types of jobs, for example you can set a job which will fire its MotechEvent every day. To achieve that you can use :code:`CronSchedulableJob`.
You can schedule job which will be fired only on several days of week, which can be achieved with :code:`DayAWeekSchedulableJob`.
Scheduler will take care of firing all the jobs at the right time, but to handle them you'll need a MotechListener.
Once MotechEvent is captured, you can do whatever you want with it, for example send an e-mail, if your module supports that.

Quartz preparation
------------------
There are two things that have to be done to make the scheduler module functional.
First, proper database needs to be created for quartz.
Second, you need to configure quartz with either :code:`quartz.properties` file or database.

| **Database creation**
| Preparing database to be used with quartz is very simple and won't take more than few minutes.

1.  Create database with name :code:`{NAME}`.
2.  | Create proper structure with proper script from :code:`/sql` sub-folder.
    | :code:`mysql -u{USER_NAME} -p{PASSWORD} {NAME} < mysql_quartz_schema_v2.1.sql` for MySQL database.
    | :code:`psql postgres_quartz_schema_v2.1.sql` for Postgresql database.

| **Quartz configuration**
| Quartz is configured with :code:`quartz.properties` file included in the module.

-   :code:`org.quartz.scheduler.instanceName`

    Scheduler name. This is only used to distinguish one scheduler from another.

-   :code:`org.quartz.threadPool.class`

    Name of the ThreadPool implementation to use. The threadpool that ships with Quartz is :code:`org.quartz.simpl.SimpleThreadPool`, and should meet the needs of nearly every user.

-   :code:`org.quartz.threadPool.threadCount`

    Number of threads available for concurrent execution of jobs.

-   :code:`org.quartz.jobStore.class`

    Class used to store scheduling information (job, triggers and calendars) within a relational database.

-   :code:`org.quartz.jobStore.driverDelegateClass`

    | Responsible for doing all JDBC work for specified database.
    | :code:`org.quartz.impl.jdbcjobstore.StdJDBCDelegate` for MySQL database.
    | :code:`org.quartz.impl.jdbcjobstore.PostgreSQLDelegate` for Postgresql database.
    | We also provide computed variable :code:`${sql.quartz.delegateClass}`, based on driver class.

-   :code:`org.quartz.jobStore.dataSource = {dataSource}`

    DataSource which JobStore should use. The value of this property must be the name of one the DataSources defined in the configuration properties file.

-   :code:`org.quartz.jobStore.tablePrefix`

    Prefix to use with quartz tables.

-   :code:`org.quartz.jobStore.driverDelegateInitString`

    Properties and their values delimited by pipe, passed to DriverDelegate during initialization.
    All delegates shipped with Quartz support property called :code:`triggerPersistenceDelegateClasses`, which can be set to a coma-separated list of classes that implement the :code:`TriggerPersistenceDelegate` interface for  storing custom trigger types.

-   :code:`org.quartz.dataSource.{dataSource}.driver`

    | JDBC driver for the chosen database.
    | :code:`com.mysql.jdbc.Driver` for MySQL database.
    | :code:`org.postgresql.Driver` for Postgresql.

-   :code:`org.quartz.dataSource.{dataSource}.URL`

    URL for connecting to database.

-   :code:`org.quartz.dataSource.{dataSource}.user`

    User name used to connect to database.

-   :code:`org.quartz.dataSource.{dataSource}.password`

    User password used to connect to database.

-   :code:`org.quartz.dataSource.{dataSource}.maxConnections`

    Maximum number of connection that DataSource can create in it's pool of connections.

Job types
---------
Scheduler makes use of job types listed below:

| **RunOnceSchedulableJob**
| Job, which will be fired only once at a date given by user.

.. csv-table::
    :header: "Type", "Name", "Description"
    :widths: 20, 30, 50

    ":code:`MotechEvent`", ":code:`motechEvent`", "Motech event which will be fired when the job triggers."
    ":code:`Date`", ":code:`startTime`", "Date at which job should become :code:`ACTIVE`."

| **CronSchedulableJob**
| Job, which will be fired on every match with given `cron expression`_.

.. csv-table::
    :header: "Type", "Name", "Description"
    :widths: 20, 30, 50

    ":code:`MotechEvent`", ":code:`motechEvent`", "Motech event which will be fired when the job triggers."
    ":code:`String`", ":code:`cronExpression`", "Standard `cron expression`_, which defines when the job should be fired."
    ":code:`Date`", ":code:`startTime`", "Date at which job should become :code:`ACTIVE`."
    ":code:`Date`", ":code:`endTime`", "Date at which job should be stopped. Should be :code:`null` if job should never end."
    ":code:`boolean`", ":code:`ignorePastFiresAtStart`", "Defines whether job should ignore past fires at start or not."


| **DayOfWeekSchedulableJob**
| Job, which will be fired at given time on days provided by user.

.. csv-table::
    :header: "Type", "Name", "Description"
    :widths: 20, 30, 50

    ":code:`MotechEvent`", ":code:`motechEvent`", "Motech event which will be fired when the job triggers."
    ":code:`LocalDate`", ":code:`start`", "Date at which job should become :code:`ACTIVE`."
    ":code:`LocalDate`", ":code:`end`", "Date at which job should be stopped. Should be :code:`null` if job should never end."
    ":code:`List<DayOfWeek>`", ":code:`days`", "List of days at which job should be fired."
    ":code:`Time`", ":code:`time`", "Time at which job should be fired. :code:`Time` is a class from :code:`org.motechproject.commons.date.model` package. It stores hour and minutes."
    ":code:`boolean`", ":code:`ignorePastFiresAtStart`", "Defines whether job should ignore past fires at start or not."

| **RepeatingSchedulableJob**
| Job, which will be fired every user-specified time interval(in milliseconds), but won't be fired more than given number of times.
.. csv-table::
    :header: "Type", "Name", "Description"
    :widths: 20, 30, 50

    ":code:`MotechEvent`", ":code:`motechEvent`", "Motech event which will be fired when the job triggers."
    ":code:`Date`", ":code:`startTime`", "Date at which job should become :code:`ACTIVE`."
    ":code:`Date`", ":code:`endTime`", "Date at which job should be stopped. Should be :code:`null` if job should never end."
    ":code:`Integer`", ":code:`repeatCount`", "Defines how many times job should be repeated, which mean that with :code:`0` if will fire once, with :code:`-1` it will fire infinite number of times and with :code:`null` it will repeat number of times predefined in :code:`MotechSchedulerServiceImpl`."
    ":code:`Long`", ":code:`repeatIntervalInMilliSeconds`", "Defines how often(in milliseconds) job should be fired."
    ":code:`boolean`", ":code:`ignorePastFiresAtStart`", "Defines whether job should ignore past fires at start or not."
    ":code:`boolean`", ":code:`useOriginalFireTimeAfterMisfire`", "Defines whether job should use original fire time after misfire."

| **RepeatingPeriodSchedulableJob**
| Job, which will be fired every, user-specified period. Period is an instance of :code:`org.joda.time.Period` class.

.. csv-table::
    :header: "Type", "Name", "Description"
    :widths: 20, 30, 50

    ":code:`MotechEvent`", ":code:`motechEvent`", "Motech event which will be fired when the job triggers."
    ":code:`Date`", ":code:`startTime`", "Date at which job should become :code:`ACTIVE`."
    ":code:`Date`", ":code:`endTime`", "Date at which job should be stopped. Should be :code:`null` if job should never end."
    ":code:`org.joda.time.Period`", ":code:`repeatPeriod`", "Defines how often job should be fired."
    ":code:`boolean`", ":code:`ignorePastFiresAtStart`", "Defines whether job should ignore past fires at start or not."
    ":code:`boolean`", ":code:`useOriginalFireTimeAfterMisfire`", "Defines whether job should use original fire time after misfire."

OSGi Services
-------------
| **Motech Schedule Service**
| Motech Scheduler Service Interface provides methods to schedule, reschedule and unschedule a job. It provides separate methods for scheduling, safe-scheduling and unscheduling every type of job.

-   | :code:`void scheduleDayOfWeekJob(DayOfWeekSchedulableJob dayOfWeekSchedulableJob);`
    | :code:`void scheduleJob(CronSchedulableJob cronSchedulableJob);`
    | :code:`void scheduleRepeatingJob(RepeatingSchedulableJob repeatingSchedulableJob);`
    | :code:`void scheduleRepeatingPeriodJob(RepeatingPeriodSchedulableJob repeatingPeriodSchedulableJob);`
    | :code:`void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob);`

    | Schedules the given schedulable job. The Job ID by which the job will be referencing in the future should be provided in an Instance of MotechEvent in SchedulableJob. If a job with the same job ID as the given exists, this job will be unscheduled and the given schedulable job will be scheduled. If you set "JobID" param in MotechEvent of a job it will be used as jobs ID.
    |

-   | :code:`void safeScheduleJob(CronSchedulableJob cronSchedulableJob);`
    | :code:`void safeScheduleRepeatingJob(RepeatingSchedulableJob repeatingSchedulableJob);`
    | :code:`void safeScheduleRepeatingPeriodJob(RepeatingPeriodSchedulableJob repeatingPeriodSchedulableJob);`
    | :code:`void safeScheduleRunOnceJob(RunOnceSchedulableJob schedulableJob);`

    Same as standard schedule methods, except that these would update existing job if one exists instead of creating a new one.

-   :code:`void rescheduleJob(String subject, String externalId, String cronExpression);`

    Reschedules a job with the given job ID to be fired according to the given Cron Expression. Previous version of the configured Motech Scheduled Event that will be created when the job is fired remains as it was.

-   | :code:`void unscheduleJob(String subject, String externalId);`
    | :code:`void unscheduleRepeatingJob(String subject, String externalId);`
    | :code:`void unscheduleRunOnceJob(String subject, String externalId);`

    Unschedules a job with the given ID.

-   :code:`void unscheduleAllJobs(String jobIdPrefix);`

    Unschedules all jobs with given prefix.

-   | :code:`void safeUnscheduleJob(String subject, String externalId);`
    | :code:`void safeUnscheduleRepeatingJob(String subject, String externalId);`
    | :code:`void safeUnscheduleRunOnceJob(String subject, String externalId);`

    Same as standard unschedule methods except that these would not throw an exception if the job doesn't exist.

-   :code:`void safeUnscheduleAllJobs(String jobIdPrefix);`

    Same as :code:`unscheduleAllJobs` except that it would not throw an exception.

-   :code:`DateTime getPreviousFireDate(JobId jobId);`

    Returns last date the job with given ID was fired.

-   :code:`DateTime getNextFireDate(JobId jobId);`

    Returns next date the job with given ID will be fired.

-   :code:`List<Date> getScheduledJobTimings(String subject, String externalJobId, Date startDate, Date endDate);`

    Returns timings between start and end dates for job with given ID.

-   :code:`List<Date> getScheduledJobTimingsWithPrefix(String subject, String externalJobIdPrefix, Date startDate, Date endDate);`

    Returns timings between start and end dates for jobs with given prefix.

-   :code:`List<JobBasicInfo> getScheduledJobsBasicInfo();`

    Returns basic information about job as a list of :code:`JobBasicInfo` instances.

-   :code:`List<JobKey> getFilteredAndSortedJobKeys(Filter filter, String sortColumn) throws SchedulerException;`

    Returns list of job keys, which are filtered and sorted using values defined in :code:`filter`.

-   :code:`JobBasicInfo getBasicInfoForJobKey(JobKey jobKey) throws SchedulerException;`

    Returns basic information about job with given :code:`jobKey`. Those information are returned as instance of :code:`JobsRecords`.

-   :code:`JobDetailedInfo getScheduledJobDetailedInfo(JobBasicInfo jobBasicInfo);`

    Returns detailed information about job with given :code:`JobBasicInfo`.

| **Examples**
| Let's say we have a module, which is able to send a SMS whenever proper MotechEvent is fired. It uses proper @MotechListener to listen for events and then handle them. We want to extend it with ability to use MotechSchedulerService and be able to schedule our own jobs. The following examples will illustrate how to achieve this. Let's add methods for sending "Hello!" message to 000000000 every day at 8:00 AM and ability to schedule the same SMS for sending it next day.

.. code-block:: java

    @Autowired
    MotechSchedulerService schedulerService;

    private MotechEvent prepareMessage() {

        //Params below are very basic information about a SMS.
        //Those params means that SMS module will send a SMS with message "Hello!" to number 000000000.
        Map<String, Object> params = new HashMap();
        params.put("message", "Hello!");
        params.put("recipient", 000000000);

        return new MotechEvent("send_SMS_now", params);
    }

    public void scheduleSendSMSJob() {

        //First, we need a MotechEvent.
        MotechEvent motechEvent = prepareMessage();

        //We'll also need a cron expression
        String cronExpression = "0 0 8 1/1 * ? *";

        //and a start date.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        //Now let' create our job.
        //We don't want it to stop so we set end date to null.
        //We also want to ignore past fires so we set ignorePastFiresAtStart flag to true.
        CronSchedulableJob cronJob = new CronSchedulableJob(motechEvent, cronExpression, tomorrow, null, true);

        //Now we need to schedule our job.
        schedulerService.safeScheduleJob(cronJob);
    }

    //Now same scenario, but we only want to send that SMS once, and we want to do it tomorow.
    public void scheduleSendSMSNowJob() {

        MotechEvent motechEvent = prepareMessage();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        //Now let' create our job
        RunOnceSchedulableJob job = new RunOnceSchedulableJob(motechEvent, tomorrow);

        schedulerService.safeScheduleRunOnceJob(job);
    }

That's it, scheduler module will take care of firing it at the right time. However, you need to have your listener ready to listen for the motech event and then handle it.


Handling of past fires
----------------------
If :code:`ignorePastFiresAtStart` is set to :code:`true` and start date is in the past, fires, which occurred before current time will be ignored. Otherwise they will be fired immediately.

Handling misfires
-----------------
If job that, for some reason, couldn't be fired at specified time will be fired as soon as possible. However, if :code:`useOrginalFireTimeAfterMisfire` is set to :code:`true` it will have it's fire date set to the original scheduled date. Otherwise it will be set to date of actual fire.

Additional resources
--------------------
-   `quartz library`_

    Quartz website containing all the information about quartz library and it's classes.

-   `cron expression`_

    Website explaining what cron expression is and how to build one.

.. _quartz library: http://www.quartz-scheduler.org/

.. _cron expression: https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm