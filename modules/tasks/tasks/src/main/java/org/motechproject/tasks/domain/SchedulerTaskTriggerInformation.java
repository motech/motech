package org.motechproject.tasks.domain;


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

    public enum schedulerJobType {
        CRON_JOB,
        REPEATING_JOB,
        RUN_ONCE_JOB,
        DAY_OF_WEEK_JOB,
        REPEATING_JOB_WITH_PERIOD_INTERVAL
    }

    @Field
    private schedulerJobType type;

    @Field
    private String startDate;

    @Field
    private String endDate;

    @Field
    private long interval;

    @Field
    private String cronExpression;

    @Field
    private int repeatCount;

    @Field
    private List<String> days;


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


    public schedulerJobType getType() {
        return type;
    }

    public void setType(schedulerJobType type) {
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

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
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

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}
