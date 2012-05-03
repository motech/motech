package org.motechproject.sms.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.sms.OutboundSMS;
import org.motechproject.sms.api.DeliveryStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class AllOutboundSMS extends MotechBaseRepository<OutboundSMS> {
    @Autowired
    protected AllOutboundSMS(@Qualifier("smppDBConnector") CouchDbConnector db) {
        super(OutboundSMS.class, db);
    }

    public void updateDeliveryStatus(String recipient, String refNo, DateTime sentOn, String deliveryStatus) {
        OutboundSMS outboundSMS = findBy(refNo, recipient, sentOn);
        outboundSMS.setStatus(DeliveryStatus.valueOf(deliveryStatus));
        update(outboundSMS);
    }

    public OutboundSMS findBy(String refNo, String phoneNumber) {
        List<OutboundSMS> smses = findAllBy(refNo, phoneNumber);
        return CollectionUtils.isEmpty(smses) ? null : smses.get(0);
    }

    @View(name = "by_recipient", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit(doc.phoneNumber, doc) }")
    public List<OutboundSMS> findBy(String phoneNumber) {
        return queryView("by_recipient", phoneNumber);
    }

    @View(name = "by_recipient_and_ref_no", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.refNo, doc.phoneNumber], doc) }")
    public List<OutboundSMS> findAllBy(String refNo, String phoneNumber) {
        return queryView("by_recipient_and_ref_no", ComplexKey.of(refNo, phoneNumber));
    }

    @View(name = "by_recipient_and_ref_no_with_message_time", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.refNo, doc.phoneNumber, doc.messageTime], doc) }")
    public OutboundSMS findBy(String refNo, String phoneNumber, DateTime deliveryTime) {
        List<OutboundSMS> smses = queryView("by_recipient_and_ref_no_with_message_time", ComplexKey.of(refNo, phoneNumber, deliveryTime));
        return CollectionUtils.isEmpty(smses) ? null : smses.get(0);
    }

    public void createOrReplace(OutboundSMS outboundSMS) {
        OutboundSMS sms = findBy(outboundSMS.getRefNo(), outboundSMS.getPhoneNumber(), outboundSMS.getMessageTime());
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

    @View(name = "within_message_time_range", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit(doc.messageTime, doc) }")
    public List<OutboundSMS> messagesSentBetween(DateTime from, DateTime to) {
        return db.queryView(createQuery("within_message_time_range").startKey(from).endKey(to).includeDocs(true), OutboundSMS.class);
    }
}
