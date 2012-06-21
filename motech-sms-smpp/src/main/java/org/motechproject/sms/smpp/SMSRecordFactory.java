package org.motechproject.sms.smpp;

import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.api.SMSType;

import java.util.ArrayList;
import java.util.List;

public class SMSRecordFactory {

    public SMSRecord map(InboundSMS inboundSMS) {
        return new SMSRecord(SMSType.INBOUND, inboundSMS.getPhoneNumber(), null, null, inboundSMS.getMessageContent(), inboundSMS.getMessageTime());
    }

    public List<SMSRecord> mapInbound(List<InboundSMS> inboundSMSes) {
        List<SMSRecord> smsRecords = new ArrayList<SMSRecord>();
        for (InboundSMS inboundSMS : inboundSMSes) {
            smsRecords.add(map(inboundSMS));
        }
        return smsRecords;
    }

    public SMSRecord map(OutboundSMS outboundSMS) {
        return new SMSRecord(SMSType.OUTBOUND, outboundSMS.getPhoneNumber(),
                outboundSMS.getDeliveryStatus(), outboundSMS.getRefNo(),
                outboundSMS.getMessageContent(), outboundSMS.getMessageTime());
    }

    public List<SMSRecord> mapOutbound(List<OutboundSMS> outboundSMSes) {
        List<SMSRecord> smsRecords = new ArrayList<SMSRecord>();
        for (OutboundSMS outboundSMS : outboundSMSes) {
            smsRecords.add(map(outboundSMS));
        }
        return smsRecords;
    }
}
