package org.motechproject.sms.api.service;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;

import java.util.List;

public interface SmsAuditService {
    List<SMSRecord> outboundMessagesFor(String phoneNumber);
    List<SMSRecord> allOutboundMessagesBetween(DateTime from, DateTime to);
    List<SMSRecord> inboundMessagesFor(String phoneNumber);
    List<SMSRecord> allInboundMessagesBetween(DateTime from, DateTime to);
}
