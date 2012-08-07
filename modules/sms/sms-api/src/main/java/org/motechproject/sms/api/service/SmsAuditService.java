package org.motechproject.sms.api.service;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;

import java.util.List;

public interface SmsAuditService {
    List<SMSRecord> allOutboundMessagesBetween(DateTime from, DateTime to);
    List<SMSRecord> allOutboundMessagesBetween(String phoneNumber, DateTime from, DateTime to);
    List<SMSRecord> allInboundMessagesBetween(DateTime from, DateTime to);
    List<SMSRecord> allInboundMessagesBetween(String phoneNumber, DateTime from, DateTime to);
}
