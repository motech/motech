package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.repository.AllInboundSMS;
import org.motechproject.sms.repository.AllOutboundSMS;
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
    public List<SMSRecord> outboundMessagesFor(String phoneNumber) {
        return new SMSRecordFactory().mapOutbound(allOutboundSMS.findBy(phoneNumber));
    }

    @Override
    public List<SMSRecord> allOutboundMessagesBetween(DateTime from, DateTime to) {
        return new SMSRecordFactory().mapOutbound(allOutboundSMS.messagesSentBetween(from, to));
    }

    @Override
    public List<SMSRecord> inboundMessagesFor(String phoneNumber) {
       return new SMSRecordFactory().mapInbound(allInboundSMS.findBy(phoneNumber));
    }

    @Override
    public List<SMSRecord> allInboundMessagesBetween(DateTime from, DateTime to) {
        return new SMSRecordFactory().mapInbound(allInboundSMS.messagesReceivedBetween(from, to));
    }
}
