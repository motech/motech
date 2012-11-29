package org.motechproject.sms.http.service;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.api.service.SmsAuditService;

import java.util.List;

public class SmsAuditServiceImpl implements SmsAuditService {
    private static final String NOT_IMPLEMENTED_MSG = "Not implemented";

    @Override
    public List<SMSRecord> allOutboundMessagesBetween(DateTime from, DateTime to) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MSG);
    }

    @Override
    public List<SMSRecord> allOutboundMessagesBetween(String phoneNumber, DateTime from, DateTime to) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MSG);
    }

    @Override
    public List<SMSRecord> allInboundMessagesBetween(DateTime from, DateTime to) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MSG);
    }

    @Override
    public List<SMSRecord> allInboundMessagesBetween(String phoneNumber, DateTime from, DateTime to) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MSG);
    }
}
