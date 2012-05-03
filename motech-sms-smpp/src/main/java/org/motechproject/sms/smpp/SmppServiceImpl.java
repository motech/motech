package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.repository.AllInboundSMS;
import org.motechproject.sms.repository.AllOutboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmppServiceImpl implements SmppService {
    private AllOutboundSMS allOutboundSMS;
    private AllInboundSMS allInboundSMS;

    @Autowired
    public SmppServiceImpl(AllOutboundSMS allOutboundSMS, AllInboundSMS allInboundSMS) {
        this.allOutboundSMS = allOutboundSMS;
        this.allInboundSMS = allInboundSMS;
    }

    @Override
    public List<SMSRecord> getOutboundMessagesFor(String phoneNumber) {
        return new SMSRecordFactory().mapOutbound(allOutboundSMS.findBy(phoneNumber));
    }

    @Override
    public List<SMSRecord> getAllOutboundMessages(DateTime from, DateTime to) {
        return new SMSRecordFactory().mapOutbound(allOutboundSMS.messagesSentBetween(from, to));
    }

    @Override
    public List<SMSRecord> getInboundMessagesFor(String phoneNumber) {
       return new SMSRecordFactory().mapInbound(allInboundSMS.findBy(phoneNumber));
    }

    @Override
    public List<SMSRecord> getAllInboundMessages(DateTime from, DateTime to) {
        return new SMSRecordFactory().mapInbound(allInboundSMS.messagesReceivedBetween(from, to));
    }
}
