package org.motechproject.mds.test.secondary.domain;

import org.motechproject.mds.test.domain.differentbundles.type.MessageStatus;

public enum CallStatus {
    INITIATED, PENDING, FINISHED, ABORTED;

    public static CallStatus createFrom(MessageStatus status) {
        return valueOf(status.toString());
    }
}
