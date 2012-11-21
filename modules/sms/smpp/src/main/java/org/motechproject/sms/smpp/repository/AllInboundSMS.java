package org.motechproject.sms.smpp.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.sms.smpp.InboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ektorp.ComplexKey.of;

@Repository
public class AllInboundSMS extends MotechBaseRepository<InboundSMS> {

    @Autowired
    protected AllInboundSMS(@Qualifier("smppDBConnector") CouchDbConnector db) {
        super(InboundSMS.class, db);
    }

    @View(name = "by_phone_number_within_message_time_range", map = "function(doc) {  if (doc.type === 'InboundSMS') emit([doc.phoneNumber, doc.messageTime], doc) }")
    public List<InboundSMS> messagesReceivedBetween(String phoneNumber, DateTime from, DateTime to) {
        return db.queryView(createQuery("by_phone_number_within_message_time_range").startKey(of(phoneNumber, from))
                .endKey(of(phoneNumber, to)).includeDocs(true), InboundSMS.class);
    }

    @View(name = "within_message_time_range", map = "function(doc) {  if (doc.type === 'InboundSMS') emit(doc.messageTime, doc) }")
    public List<InboundSMS> messagesReceivedBetween(DateTime from, DateTime to) {
        return db.queryView(createQuery("within_message_time_range").startKey(from)
                .endKey(to).includeDocs(true), InboundSMS.class);
    }
}
