package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.smpp.repository.AllInboundSMS;
import org.motechproject.sms.smpp.repository.AllOutboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmppAuditServiceImpl implements SmsAuditService {
    private AllOutboundSMS allOutboundSMS;
    private AllInboundSMS allInboundSMS;

    @Autowired
    public SmppAuditServiceImpl(AllOutboundSMS allOutboundSMS, AllInboundSMS allInboundSMS) {
        this.allOutboundSMS = allOutboundSMS;
        this.allInboundSMS = allInboundSMS;
    }

    @Override
    public List<SMSRecord> allOutboundMessagesBetween(DateTime from, DateTime to) {
        return new SMSRecordFactory().mapOutbound(allOutboundSMS.messagesSentBetween(from, to));
    }

    @Override
    public List<SMSRecord> allOutboundMessagesBetween(String phoneNumber, DateTime from, DateTime to) {
        return new SMSRecordFactory().mapOutbound(allOutboundSMS.messagesSentBetween(phoneNumber, from, to));
    }

    @Override
    public List<SMSRecord> allInboundMessagesBetween(DateTime from, DateTime to) {
        return new SMSRecordFactory().mapInbound(allInboundSMS.messagesReceivedBetween(from, to));
    }

    @Override
    public List<SMSRecord> allInboundMessagesBetween(String phoneNumber, DateTime from, DateTime to) {
        return new SMSRecordFactory().mapInbound(allInboundSMS.messagesReceivedBetween(phoneNumber, from, to));
    }
}
