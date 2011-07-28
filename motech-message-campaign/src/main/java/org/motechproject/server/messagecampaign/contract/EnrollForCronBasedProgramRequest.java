package org.motechproject.server.messagecampaign.contract;

import java.util.Date;

public class EnrollForCronBasedProgramRequest extends EnrollRequest {

    private Date referenceDate;

    public Date referenceDate() {
        if(referenceDate != null)
            return referenceDate;
        return new Date();
    }

    public void referenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }
}
