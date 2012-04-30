package org.motechproject.sms.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.sms.DeliveryStatus;
import org.motechproject.sms.OutboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Repository
public class AllOutboundSMS extends MotechBaseRepository<OutboundSMS> {
    @Autowired
    protected AllOutboundSMS(@Qualifier("smppDBConnector") CouchDbConnector db) {
        super(OutboundSMS.class, db);
    }

    public void updateDeliveryStatus(String recipient, String refNo, String deliveryStatus) {
        OutboundSMS outboundSMS = findBy(refNo, recipient);
        outboundSMS.setStatus(DeliveryStatus.valueOf(deliveryStatus));
        update(outboundSMS);
    }

    public OutboundSMS findBy(String smscRefNo, String phoneNumber) {
        List<OutboundSMS> smses = findAllBy(smscRefNo, phoneNumber);
        return CollectionUtils.isEmpty(smses) ? null : smses.get(0);
    }

    @View(name = "by_recipient_and_ref_no", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.smscRefNo, doc.phoneNumber], doc) }")
    public List<OutboundSMS> findAllBy(String smscRefNo, String phoneNumber) {
        return queryView("by_recipient_and_ref_no", ComplexKey.of(smscRefNo, phoneNumber));
    }

    @View(name = "by_recipient_and_ref_no_with_message_time", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.smscRefNo, doc.phoneNumber, doc.messageTime], doc) }")
    public OutboundSMS findBy(String smscRefNo, String phoneNumber, Date deliveryTime) {
        List<OutboundSMS> smses = queryView("by_recipient_and_ref_no_with_message_time", ComplexKey.of(smscRefNo, phoneNumber, deliveryTime));
        return CollectionUtils.isEmpty(smses) ? null : smses.get(0);
    }

    public void createOrReplace(OutboundSMS outboundSMS) {
        OutboundSMS sms = findBy(outboundSMS.getSmscRefNo(), outboundSMS.getPhoneNumber(), outboundSMS.getMessageTime());
        if (null == sms)
            add(outboundSMS);
        else
            update(copyDocumentIdInfo(sms, outboundSMS));
    }

    private OutboundSMS copyDocumentIdInfo(OutboundSMS source, OutboundSMS target) {
        target.setId(source.getId());
        target.setRevision(source.getRevision());
        return target;
    }
}
