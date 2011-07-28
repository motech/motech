package org.motechproject.server.messagecampaign.contract;

import org.motechproject.model.Time;

public class EnrollForAbsoluteProgramRequest extends EnrollRequest {

    private Time reminderTime;

    public void reminderTime(Time reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Time reminderTime() {
        return this.reminderTime;
    }
}
