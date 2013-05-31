package org.motechproject.ivr.domain;

import org.joda.time.DateTime;

public interface CallDetail {

    String getCallId();

    String getPhoneNumber();

    DateTime getStartDate();

    DateTime getEndDate();
}
