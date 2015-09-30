package org.motechproject.tasks.domain;


import org.joda.time.Period;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import java.util.List;


/**
 * Extends TaskTriggerInformation representation of a single task trigger by adding scheduleTaskParameters used in
 * scheduling this trigger. It is a part of the task model.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
@Inheritance(strategy= InheritanceStrategy.SUPERCLASS_TABLE)
public class SchedulerTaskTriggerInformation extends TaskTriggerInformation {

    private static final long serialVersionUID = 4921423073444417178L;

    public enum SchedulerJobType {
        CRON_JOB,
        REPEATING_JOB,
        RUN_ONCE_JOB,
        DAY_OF_WEEK_JOB,
        REPEATING_JOB_WITH_PERIOD_INTERVAL
    }

    @Field
    private SchedulerJobType type;

    @Field
    private String startDate;

    @Field
    private String endDate;

    @Field
    private int interval;

    @Field
    private String cronExpression;

    @Field
    private int repeatCount;

    @Field
    private List<DayOfWeek> days;

    @Field
    private boolean ignoreFiresignorePastFiresAtStart;

    @Field
    private Time time;

    @Field
    private Period repeatPeriod;


    /**
     * Constructor
     */
    public SchedulerTaskTriggerInformation() {
        this(null, null, null, null, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the trigger display name
     * @param channelName  the trigger channel name
     * @param moduleName  the trigger module name
     * @param moduleVersion  the module version
     * @param subject  the trigger subject
     * @param triggerListener  the trigger listener
     */
    public SchedulerTaskTriggerInformation(String displayName, String channelName, String moduleName,
                                           String moduleVersion, String subject, String triggerListener,
                                           String startDate) {
        super(displayName, channelName, moduleName, moduleVersion, subject, triggerListener);
        this.startDate = startDate;
    }


    public SchedulerJobType getType() {
        return type;
    }

    public void setType(SchedulerJobType type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public List<DayOfWeek> getDays() {
        return days;
    }

    public void setDays(List<DayOfWeek> days) {
        this.days = days;
    }

    public boolean isIgnoreFiresignorePastFiresAtStart() {
        return ignoreFiresignorePastFiresAtStart;
    }

    public void setIgnoreFiresignorePastFiresAtStart(boolean ignoreFiresignorePastFiresAtStart) {
        this.ignoreFiresignorePastFiresAtStart = ignoreFiresignorePastFiresAtStart;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Period getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(Period repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }
}
