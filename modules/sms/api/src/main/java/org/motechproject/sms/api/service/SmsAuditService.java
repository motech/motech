package org.motechproject.sms.api.service;

import org.motechproject.sms.api.domain.SmsRecord;

import java.util.List;

public interface SmsAuditService {

    void log(SmsRecord smsRecord);

    void updateDeliveryStatus(String recipient, String refNo, String name);

    List<SmsRecord> findAllSmsRecords();

    List<SmsRecord> findAllSmsRecords(SmsRecordSearchCriteria criteria);
}
