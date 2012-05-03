package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;

import java.util.List;

public interface SmppService {
    List<SMSRecord> getOutboundMessagesFor(String phoneNumber);
    List<SMSRecord> getAllOutboundMessages(DateTime from, DateTime to);
    List<SMSRecord> getInboundMessagesFor(String phoneNumber);
    List<SMSRecord> getAllInboundMessages(DateTime from, DateTime to);
}
