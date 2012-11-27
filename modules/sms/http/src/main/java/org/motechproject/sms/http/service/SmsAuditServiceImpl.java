package org.motechproject.sms.http.service;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.api.service.SmsAuditService;

import java.util.List;

public class SmsAuditServiceImpl implements SmsAuditService {

    @Override
    public List<SMSRecord> allOutboundMessagesBetween(DateTime from, DateTime to) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<SMSRecord> allOutboundMessagesBetween(String phoneNumber, DateTime from, DateTime to) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<SMSRecord> allInboundMessagesBetween(DateTime from, DateTime to) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<SMSRecord> allInboundMessagesBetween(String phoneNumber, DateTime from, DateTime to) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
