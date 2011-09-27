package org.motechproject.ivr.kookoo.service;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;

public interface KookooCallDetailRecordsService {

    public KookooCallDetailRecord findByCallId(String callId);
}
