package org.motechproject.scheduletracking.api.domain.factory;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.contract.EnrolmentRequest;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;

import java.util.Date;

public class EnrolmentFactory {
    public static Enrolment newEnrolment(EnrolmentRequest enrolmentRequest) {
        return new Enrolment(enrolmentRequest.externalId(), LocalDate.now(), enrolmentRequest.scheduleName());
    }
}
