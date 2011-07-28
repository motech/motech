package org.motechproject.server.messagecampaign.contract;

import org.motechproject.model.Time;

import java.util.Date;

public class EnrollForRelativeProgramRequest extends EnrollRequest {

    private Date referenceDate;

    private Time reminderTime;

    public Date referenceDate() {
        if (referenceDate != null)
            return referenceDate;
        return new Date();
    }

    public void referenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }

    public void reminderTime(Time reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Time reminderTime() {
        return this.reminderTime;
    }
}
