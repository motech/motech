package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Date;

@TypeDiscriminator("doc.type === 'Reminder'")
public class Reminder extends MotechBaseDataObject {
    public enum intervalUnits {SECONDS, MINUTES, HOURS, DAYS, WEEKS}

    private String appointmentId;
    private String externalId;
    private Date startDate;
    private Date endDate;
    private int intervalCount;
    private intervalUnits units;
    private int repeatCount;
    private String jobId;

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(int intervalCount) {
        this.intervalCount = intervalCount;
    }

    public intervalUnits getUnits() {
        return units;
    }

    public void setUnits(intervalUnits units) {
        this.units = units;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @JsonIgnore
    public long getIntervalSeconds() {
        long ret = -1;

        if (intervalUnits.SECONDS == units) {
            ret = intervalCount;
        }

        if (intervalUnits.MINUTES == units) {
            ret = (intervalCount * 60);
        }

        if (intervalUnits.HOURS == units) {
            ret = (intervalCount * 60 * 60);
        }

        if (intervalUnits.DAYS == units) {
            ret = (intervalCount * 60 * 60 * 24);
        }

        if (intervalUnits.WEEKS == units) {
            ret = (intervalCount * 60 * 60 * 24 * 7);
        }

        return ret;
    }
}