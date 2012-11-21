package org.motechproject.sms.smpp.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.smpp.OutboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static java.util.Collections.reverseOrder;
import static org.ektorp.ComplexKey.of;

@Repository
public class AllOutboundSMS extends MotechBaseRepository<OutboundSMS> {

    @Autowired
    protected AllOutboundSMS(@Qualifier("smppDBConnector") CouchDbConnector db) {
        super(OutboundSMS.class, db);
    }

    public void updateDeliveryStatus(String recipient, String refNo, String deliveryStatus) {
        OutboundSMS outboundSMS = findLatestBy(refNo, recipient);
        outboundSMS.setStatus(DeliveryStatus.valueOf(deliveryStatus));
        update(outboundSMS);
    }

    @View(name = "find_latest_by_recipient_and_ref_no", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.messageTime, doc.refNo, doc.phoneNumber], doc) }")
    public OutboundSMS findLatestBy(String refNo, String phoneNumber) {
        final List<OutboundSMS> outboundSMSes = findAllBy(refNo, phoneNumber);
        return CollectionUtils.isEmpty(outboundSMSes) ? null : (OutboundSMS) sort(outboundSMSes, on(OutboundSMS.class).getMessageTime(), reverseOrder()).get(0);
    }

    @View(name = "by_recipient_and_ref_no", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.refNo, doc.phoneNumber], doc) }")
    public List<OutboundSMS> findAllBy(String refNo, String phoneNumber) {
        return queryView("by_recipient_and_ref_no", of(refNo, phoneNumber));
    }

    @View(name = "by_message_time_and_recipient_and_ref_no_", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.messageTime, doc.refNo, doc.phoneNumber], doc) }")
    public OutboundSMS findBy(DateTime deliveryTime, String refNo, String phoneNumber) {
        List<OutboundSMS> smses = queryView("by_message_time_and_recipient_and_ref_no_", of(deliveryTime, refNo, phoneNumber));
        return CollectionUtils.isEmpty(smses) ? null : smses.get(0);
    }

    @View(name = "by_phone_number_within_message_time_range", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit([doc.phoneNumber, doc.messageTime], doc) }")
    public List<OutboundSMS> messagesSentBetween(String phoneNumber, DateTime from, DateTime to) {
        return db.queryView(createQuery("by_phone_number_within_message_time_range").startKey(of(phoneNumber, from))
                .endKey(of(phoneNumber, to)).includeDocs(true), OutboundSMS.class);
    }

    @View(name = "within_message_time_range", map = "function(doc) {  if (doc.type === 'OutboundSMS') emit(doc.messageTime, doc) }")
    public List<OutboundSMS> messagesSentBetween(DateTime from, DateTime to) {
        return db.queryView(createQuery("within_message_time_range").startKey(from)
                .endKey(to).includeDocs(true), OutboundSMS.class);
    }

    public void createOrReplace(OutboundSMS outboundSMS) {
        OutboundSMS sms = findBy(outboundSMS.getMessageTime(), outboundSMS.getRefNo(), outboundSMS.getPhoneNumber());
        if (null == sms) {
            add(outboundSMS);
        } else {
            update(copyDocumentIdInfo(sms, outboundSMS));
        }
    }

    private OutboundSMS copyDocumentIdInfo(OutboundSMS source, OutboundSMS target) {
        target.setId(source.getId());
        target.setRevision(source.getRevision());
        return target;
    }
}
