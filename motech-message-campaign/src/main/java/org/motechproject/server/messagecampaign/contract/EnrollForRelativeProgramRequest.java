package org.motechproject.server.messagecampaign.contract;

import java.util.Date;

public class EnrollForRelativeProgramRequest extends EnrollRequest {

    private Date referenceDate;

    public Date referenceDate() {
        return referenceDate;
    }

    public void referenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }
}
